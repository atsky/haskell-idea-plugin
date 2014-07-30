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

public class HaskellCommandLineState(environment: ExecutionEnvironment, val configuration: CabalRunConfiguration) : CommandLineState(environment) {


    protected override fun startProcess(): ProcessHandler {
        val generalCommandLine = createCommandLine()

        return JavaCommandLineStateUtil.startProcess(generalCommandLine)
    }

    private fun startGHCiDebugProcess(): HaskellDebugProcessHandler {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Module not specified")
        }

        val baseDir = module.getModuleFile()!!.getParent()!!.getCanonicalPath()!!
        val srcDir = joinPath(baseDir, "src")
        val filePath = joinPath(baseDir, "src", "Main.hs")
        val ghciPath = GHCUtil.getCommandPath(ModuleRootManager.getInstance(module)!!.getSdk()!!.getHomeDirectory(), "ghci");

        val process = Runtime.getRuntime().exec(ghciPath + " " + filePath + " -i" + srcDir)
        return GHCiProcessHandler(process)
    }

    private fun startRemoteDebugProcess(): HaskellDebugProcessHandler {
        val module = configuration.getModule()
        if (module == null) {
            throw ExecutionException("Module not specified")
        }

        val streamHandler = RemoteDebugStreamHandler()
        streamHandler.start()

        val baseDir = module.getModuleFile()!!.getParent()!!.getCanonicalPath()!!
        val debuggerPath = joinPath(baseDir, "HaskellDebugger") // temporary location

        val builder = ProcessBuilder(debuggerPath, "-p${streamHandler.getPort()}")
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

}
