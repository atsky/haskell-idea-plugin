package org.jetbrains.haskell.debugger.repl

import org.jetbrains.haskell.repl.HaskellConsoleView
import com.intellij.openapi.project.Project
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.prochandlers.HaskellDebugProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.haskell.repl.HaskellConsoleEnterAction
import com.intellij.execution.process.ConsoleHistoryModel


public class DebugConsoleFactory {
    companion object {

        public fun createDebugConsole(project: Project,
                                      processHandler: HaskellDebugProcessHandler): HaskellConsoleView {
            val consoleView = HaskellConsoleView(project, "Debug REPL console", ConsoleHistoryModel())

            ProcessTerminatedListener.attach(processHandler)

            processHandler.addProcessListener(object : ProcessAdapter() {
                override fun processTerminated(event: ProcessEvent?) {
                    consoleView.getConsole().setPrompt("")
                    consoleView.getConsole().getConsoleEditor().setRendererMode(true)
                    ApplicationManager.getApplication()?.invokeLater(object : Runnable {
                        override fun run() {
                            consoleView.getConsole().getConsoleEditor().getComponent().updateUI()
                        }
                    })
                }
            })

            consoleView.attachToProcess(processHandler)

            return consoleView
        }
    }
}