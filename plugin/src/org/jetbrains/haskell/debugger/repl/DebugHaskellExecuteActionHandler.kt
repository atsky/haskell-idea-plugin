package org.jetbrains.haskell.debugger.repl

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.openapi.project.Project
import org.jetbrains.haskell.repl.HaskellConsoleExecuteActionHandler

public class DebugHaskellExecuteActionHandler(val debugProcess: HaskellDebugProcess,
                                              project: Project,
                                              preserveMarkup: Boolean) :
        HaskellConsoleExecuteActionHandler(project, preserveMarkup) {

    override fun processLine(line: String?) {
        debugProcess.debugger.trace(line)
    }
}