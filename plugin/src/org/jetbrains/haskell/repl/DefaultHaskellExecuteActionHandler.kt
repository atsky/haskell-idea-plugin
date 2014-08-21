package org.jetbrains.haskell.repl

import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.project.Project

public class DefaultHaskellExecuteActionHandler(val processHandler: ProcessHandler,
                                                project: Project,
                                                preserveMarkup: Boolean) :
        HaskellConsoleExecuteActionHandler(project, preserveMarkup) {

    override fun processLine(line: String?) {
        val os = processHandler.getProcessInput()
        if (os != null) {
            val bytes = (line + "\n").getBytes()
            os.write(bytes)
            os.flush()
        }
    }
}