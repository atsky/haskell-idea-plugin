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

public class HaskellCommandLineState(environment: ExecutionEnvironment, val configuration: CabalRunConfiguration) : CommandLineState(environment) {


    protected override fun startProcess(): ProcessHandler {
        val generalCommandLine = createCommandLine()

        return JavaCommandLineStateUtil.startProcess(generalCommandLine)
    }



    private fun createCommandLine(): GeneralCommandLine {
        val module = configuration.getModule()

        if (module == null) {
            throw ExecutionException("Module not specified")
        }

        val name = configuration.getMyExecutableName()!!
        val commandLine = GeneralCommandLine()

        val baseDir = module.getModuleFile()!!.getParent()!!.getCanonicalPath()

        val executableName = if (OS.isWindows) name + ".exe" else name
        val exePath = joinPath(baseDir!!, "dist", "build", name, executableName)

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
