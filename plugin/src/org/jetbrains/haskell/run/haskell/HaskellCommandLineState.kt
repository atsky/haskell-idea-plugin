package org.jetbrains.haskell.run.haskell

import com.intellij.execution.CantRunException
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import java.io.File
import org.jetbrains.haskell.util.joinPath
import org.jetbrains.haskell.util.OS
import com.intellij.execution.Executor
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ExecutionResult
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.configurations.RunnerSettings
import org.jetbrains.haskell.util.GHCUtil
import com.intellij.openapi.roots.ModuleRootManager
import org.jetbrains.haskell.debugger.prochandlers.GHCiProcessHandler
import org.jetbrains.haskell.debugger.prochandlers.HaskellDebugProcessHandler
import org.jetbrains.haskell.debugger.procdebuggers.utils.RemoteDebugStreamHandler
import org.jetbrains.cabal.CabalInterface
import com.intellij.openapi.module.Module
import org.jetbrains.cabal.CabalFile
import org.jetbrains.cabal.psi.Executable
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.haskell.debugger.config.HaskellDebugSettings
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.process.OSProcessHandler
import com.pty4j.PtyProcess
import org.jetbrains.haskell.config.HaskellSettings
import org.jetbrains.cabal.psi.FullVersionConstraint
import java.util.ArrayList
import org.jetbrains.haskell.debugger.prochandlers.RemoteProcessHandler
import com.intellij.openapi.vfs.CharsetToolkit

public class HaskellCommandLineState(environment: ExecutionEnvironment, val configuration: CabalRunConfiguration) : CommandLineState(environment) {


    protected override fun startProcess(): ProcessHandler {
        val generalCommandLine = createCommandLine()
        val processHandler: ProcessHandler =
                if (HaskellSettings.getInstance().getState().usePty!!) {
                    OSProcessHandler(getPtyProcess(generalCommandLine)!!, generalCommandLine.getCommandLineString(),
                            CharsetToolkit.UTF8_CHARSET)
                } else {
                    OSProcessHandler(generalCommandLine)
                }
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    private fun getPtyProcess(generalCommandLine: GeneralCommandLine): PtyProcess? {
        val exePath = generalCommandLine.getExePath()!!
        val params = generalCommandLine.getParametersList()?.getList()
        val env = generalCommandLine.getEnvironment()
        val dir = generalCommandLine.getWorkDirectory()
        val command = Array(1 + (params?.size ?: 0), {
            (i: Int) ->
            if (i == 0) exePath else params!!.get(i - 1)
        })
        return PtyProcess.exec(command, env, dir?.getAbsolutePath(), true)
    }

    private fun getDependencies(): List<FullVersionConstraint> {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Error while starting debug process: module not specified")
        }
        val exec = getExecutable(module)
        return exec.getBuildDepends() ?: listOf()
    }

    /**
     * Returns pair of main file path and path to the sources directory
     */
    private fun getPaths(): Pair<String, String> {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Error while starting debug process: module not specified")
        }
        val exec = getExecutable(module)
        val mainFileName: String? = exec.getMainFile()?.getText()
        if (mainFileName == null) {
            throw ExecutionException("Error while starting debug process: no main file specified in executable section")
        }
        val srcDirPath = tryGetSrcDirFullPath(mainFileName, module, exec)
        if (srcDirPath == null) {
            throw ExecutionException("Error while starting debug process: main file $mainFileName not found in source directories")
        }
        val filePath = joinPath(srcDirPath, mainFileName)
        return Pair(filePath, srcDirPath)
    }

    private fun getExecutable(module: Module): Executable {

        val cabalFile = tryGetCabalFile(module)
        if (cabalFile == null) {
            throw ExecutionException("Error while starting debug process: cabal file not found")
        }
        val exec = tryGetExecWithNameFromConfig(cabalFile)
        if (exec == null) {
            throw ExecutionException("Error while starting debug process: cabal file does not contain executable ${configuration.getMyExecutableName()}")
        }
        return exec
    }

    private fun startGHCiDebugProcess(): HaskellDebugProcessHandler {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Error while starting debug process: module not specified")
        }
        val paths = getPaths()
        val filePath = paths.first
        val srcDirPath = paths.second
        val ghciPath = GHCUtil.getCommandPath(ModuleRootManager.getInstance(module)!!.getSdk()!!.getHomeDirectory(), "ghci")
        if (ghciPath == null) {
            throw ExecutionException("Error while starting debug process: ghci path not specified")
        }

        val command: ArrayList<String> = arrayListOf(ghciPath, filePath, "-i$srcDirPath")
        val depends = getDependencies()
        command.add("-package")
        command.add("network")
        for (dep in depends) {
            command.add("-package")
            command.add(dep.getBaseName())
        }
        val process = Runtime.getRuntime().exec(command.copyToArray())
        return GHCiProcessHandler(process)
    }

    private fun startRemoteDebugProcess(): HaskellDebugProcessHandler {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Error while starting debug process: module not specified")
        }
        val paths = getPaths()
        val filePath = paths.first
        val srcDirPath = paths.second

        val streamHandler = RemoteDebugStreamHandler()
        streamHandler.start()

        val baseDir = module.getModuleFile()!!.getParent()!!.getCanonicalPath()!!
        val debuggerPath = HaskellDebugSettings.getInstance().getState().remoteDebuggerPath
        if (debuggerPath == null) {
            throw ExecutionException("Cannot run remote debugger: path not specified")
        }

        val command: ArrayList<String> = arrayListOf(debuggerPath, "-m${filePath}", "-p${streamHandler.getPort()}", "-i${srcDirPath}")
        val depends = getDependencies()
        for (dep in depends) {
            command.add("-pkg${dep.getBaseName()}")
        }
        val builder = ProcessBuilder(command).directory(File(baseDir))

        try {
            return RemoteProcessHandler(builder.start(), streamHandler)
        } catch (ex: Exception) {
            throw ExecutionException("Cannot run remote debugger in path " + debuggerPath)
        }
    }

    public fun executeDebug(executor: Executor, runner: ProgramRunner<out RunnerSettings>): ExecutionResult {
        val processHandler =
                if (HaskellDebugSettings.getInstance().getState().debuggerType == HaskellDebugSettings.DebuggerType.GHCI)
                    startGHCiDebugProcess()
                else
                    startRemoteDebugProcess()
        val console = createConsole(executor)
        console?.attachToProcess(processHandler)

        return DefaultExecutionResult(console, processHandler)
    }

    private fun createCommandLine(): GeneralCommandLine {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Module not specified")
        }

        val name = configuration.getMyExecutableName()!!
        val commandLine = GeneralCommandLine()

        val baseDir = module.getModuleFile()!!.getParent()!!.getCanonicalPath()

        val exePath = joinPath(baseDir!!, "dist", "build", name, OS.getExe(name))

        if (!File(exePath).exists()) {
            throw CantRunException("Cannot run: " + exePath)
        }

        val path = File(module.getModuleFilePath())
        commandLine.setWorkDirectory(path.getParent())
        commandLine.setExePath(exePath)
        val parameters = configuration.getProgramParameters()
        if (parameters != null) {
            commandLine.getParametersList()!!.addParametersString(parameters)
        }
        return commandLine
    }

    private fun tryGetCabalFile(module: Module): CabalFile? {
        val project = configuration.getProject()
        if (project == null) {
            return null
        }
        val cabalVirtualFile = CabalInterface.findCabal(module)
        if (cabalVirtualFile == null) {
            return null
        }
        val cabalInterface = CabalInterface(project)
        return cabalInterface.getPsiFile(cabalVirtualFile)
    }

    private fun tryGetExecWithNameFromConfig(cabalFile: CabalFile): Executable? {
        val execs = cabalFile.getExecutables()
        val neededExecName = configuration.getMyExecutableName()
        for (exec in execs) {
            if (exec.getExecutableName() == neededExecName) {
                return exec
            }
        }
        return null
    }

    private fun tryGetSrcDirFullPath(mainFileName: String, module: Module, correspondingExec: Executable): String? {
        val baseDirPath = module.getModuleFile()!!.getParent()!!.getCanonicalPath()!!
        val srcDirs: List<String> = correspondingExec.getHSSourceDirs()!!.map { it.getText() }
        for (srcDir in srcDirs) {
            val path = joinPath(baseDirPath, srcDir, mainFileName)
            val vFile = LocalFileSystem.getInstance()!!.findFileByIoFile(File(path))
            if (vFile != null && vFile.exists()) {
                return joinPath(baseDirPath, srcDir)
            }
        }
        return null
    }

}
