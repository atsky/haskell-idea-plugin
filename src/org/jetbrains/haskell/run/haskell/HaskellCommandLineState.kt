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
import org.jetbrains.haskell.compiler.GHCInterface
import java.io.File
import org.jetbrains.cabal.findCabal

class HaskellCommandLineState(environment: ExecutionEnvironment, val configuration: CabalRunConfiguration) : CommandLineState(environment) {


    protected override fun startProcess(): ProcessHandler {
        return JavaCommandLineStateUtil.startProcess(createCommandLine())
    }
    private fun createCommandLine(): GeneralCommandLine {
        val module = configuration.getModule()!!

        val psiFile = findCabal(module)?.getPsiFile()!!
        val name = psiFile.getExecutables()[0].getExecutableName()

        val commandLine = GeneralCommandLine()

        val baseDir = module.getModuleFile()!!.getParent()!!.getCanonicalPath()
        val exePath = baseDir + File.separator + "dist" + File.separator + "build" + File.separator + name + File.separator + name

        if (!File(exePath).exists()) {
            throw CantRunException("Cannot find runghc executable")
        }

        val path = File(module.getModuleFilePath())
        commandLine.setWorkDirectory(path.getParent())
        commandLine.setExePath(exePath)
        commandLine.addParameter(configuration.getMainFile()!!)
        return commandLine
    }

}
