package org.jetbrains.haskell.run.haskell

import com.intellij.execution.CantRunException
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.JavaCommandLineStateUtil
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.module.Module
import org.jetbrains.cabal.CabalFile
import java.io.File
import org.jetbrains.haskell.util.joinPath
import org.jetbrains.haskell.util.OS
import com.intellij.notification.Notification
import com.intellij.execution.process.BaseOSProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.Executor
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ExecutionResult
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.configurations.RunnerSettings

public class HaskellCommandLineState(environment: ExecutionEnvironment, val configuration: CabalRunConfiguration) : CommandLineState(environment) {


    protected override fun startProcess(): ProcessHandler {
        val generalCommandLine = createCommandLine()

        return JavaCommandLineStateUtil.startProcess(generalCommandLine)
    }

    protected fun startDebugProcess(): ProcessHandler {
        val filePath = "/home/vlad/coding/haskell-idea-plugin/haskell-ide-api/src/Main.hs"
        val process = Runtime.getRuntime().exec("ghci " + filePath)
        return OSProcessHandler(process)
    }

    public fun executeDebug(executor: Executor, runner: ProgramRunner<out RunnerSettings>): ExecutionResult {
        val processHandler = startDebugProcess()
        val console = createConsole(executor)
        console?.attachToProcess(processHandler)

        val output = processHandler.getProcessInput()!!
        output.write("main".toByteArray())
        output.flush()

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

        println(baseDir)
        println(File("./").getCanonicalPath())
        println(name)

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
