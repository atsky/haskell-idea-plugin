package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.GHCiDebugProcess
import com.intellij.xdebugger.frame.XSuspendContext
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque

/**
 * Created by vlad on 7/17/14.
 */

public abstract class FlowCommand : AbstractCommand() {

    override fun handleOutput(output: Deque<String?>, debugProcess: GHCiDebugProcess) {
        val filePosition = Parser.tryParseStoppedAt(output)
        if (filePosition != null) {
            val lineNumber = filePosition.startLine
            val breakpoint = debugProcess.getBreakpointAtLine(lineNumber)!!
            val context = object : XSuspendContext() {}
            debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
        }
    }
}
