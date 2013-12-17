package org.jetbrains.haskell.run.cmd

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.JavaCommandLineStateUtil
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.module.Module
import java.io.File

class SimpleCommandLineState(environment: ExecutionEnvironment,
                             val configuration: CmdLineRunConfiguration) : CommandLineState(environment) {
    protected override fun startProcess(): ProcessHandler {
        return JavaCommandLineStateUtil.startProcess(createCommandLine())
    }

    private fun createCommandLine(): GeneralCommandLine {
        val commandLine = GeneralCommandLine()
        val module = configuration.getModule()!!
        val path = File(module.getModuleFilePath())
        commandLine.setWorkDirectory(path.getParent())
        commandLine.setExePath(configuration.getExecFile()!!)
        val parameters = configuration.getProgramParameters()
        if (parameters != null) {
            commandLine.addParameter(parameters)
        }
        return commandLine
    }


}
