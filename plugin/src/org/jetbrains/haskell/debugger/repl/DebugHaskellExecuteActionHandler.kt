package org.jetbrains.haskell.debugger.repl

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.openapi.project.Project
import org.jetbrains.haskell.repl.HaskellConsoleExecuteActionHandler

public class DebugHaskellExecuteActionHandler(val debugProcess: HaskellDebugProcess,
                                              project: Project,
                                              preserveMarkup: Boolean) :
        HaskellConsoleExecuteActionHandler(project, preserveMarkup) {

    override fun processLine(line: String?) {
        if (line != null && line.trim().length > 0) {
            if (debugProcess.isReadyForNextCommand()) {
                debugProcess.startTrace(line)
            } else {
                val output = debugProcess.getProcessHandler().getProcessInput()
                output?.write(line.getBytes())
                output?.flush()
            }
        }
    }
}