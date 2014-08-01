package org.jetbrains.haskell.run.haskell

import com.intellij.execution.CantRunException
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.JavaCommandLineStateUtil
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
import org.jetbrains.haskell.debugger.GHCiProcessHandler
import org.jetbrains.haskell.debugger.HaskellDebugProcessHandler
import org.jetbrains.haskell.debugger.RemoteDebugStreamHandler
import org.jetbrains.haskell.debugger.RemoteProcessHandler
import org.jetbrains.cabal.CabalInterface
import com.intellij.openapi.module.Module
import org.jetbrains.cabal.CabalFile
import org.jetbrains.cabal.psi.Executable
import com.intellij.openapi.vfs.LocalFileSystem

public class HaskellCommandLineState(environment: ExecutionEnvironment, val configuration: CabalRunConfiguration) : CommandLineState(environment) {


    protected override fun startProcess(): ProcessHandler {
        val generalCommandLine = createCommandLine()

        return JavaCommandLineStateUtil.startProcess(generalCommandLine)
    }

    /**
     * Returns pair of main file path and path ro the sources directory
     */
    private fun getPaths(): Pair<String, String> {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Error while starting debug process: module not specified")
        }
        val cabalFile = tryGetCabalFile(module)
        if (cabalFile == null) {
            throw ExecutionException("Error while starting debug process: cabal file not found")
        }
        val exec = tryGetExecWithNameFromConfig(cabalFile)
        if(exec == null) {
            throw ExecutionException("Error while starting debug process: cabal file does not contain executable ${configuration.getMyExecutableName()}")
        }
        val mainFileName: String? = exec.getMainFile()?.getText()
        if(mainFileName == null) {
            throw ExecutionException("Error while starting debug process: no main file specified in executable section")
        }
        val srcDirPath = tryGetSrcDirFullPath(mainFileName, module, exec)
        if(srcDirPath == null) {
            throw ExecutionException("Error while starting debug process: main file $mainFileName not found in source directories")
        }
        val filePath = joinPath(srcDirPath, mainFileName)
        return Pair(filePath, srcDirPath)
    }

    private fun startGHCiDebugProcess(): HaskellDebugProcessHandler {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Error while starting debug process: module not specified")
        }
        val paths = getPaths()
        val filePath = paths.first
        val srcDirPath = paths.second
        val ghciPath = GHCUtil.getCommandPath(ModuleRootManager.getInstance(module)!!.getSdk()!!.getHomeDirectory(), "ghci");

        val process = Runtime.getRuntime().exec(ghciPath + " " + filePath + " -i" + srcDirPath)
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
        val debuggerPath = joinPath(baseDir, "HaskellDebugger") // temporary location

        val builder = ProcessBuilder(debuggerPath, "-m${filePath}", "-p${streamHandler.getPort()}", "-i${srcDirPath}")
                .directory(File(baseDir))

        return RemoteProcessHandler(builder.start(), streamHandler)
    }

    public fun executeDebug(executor: Executor, runner: ProgramRunner<out RunnerSettings>): ExecutionResult {
        val processHandler = startGHCiDebugProcess()
//        val processHandler = startRemoteDebugProcess()
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
        for(srcDir in srcDirs) {
            val path = joinPath(baseDirPath, srcDir, mainFileName)
            val vFile = LocalFileSystem.getInstance()!!.findFileByIoFile(File(path))
            if(vFile != null && vFile.exists()) {
                return joinPath(baseDirPath, srcDir)
            }
        }
        return null
    }

}
