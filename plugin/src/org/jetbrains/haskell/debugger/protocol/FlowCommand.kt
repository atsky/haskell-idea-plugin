package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.frame.XSuspendContext
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import java.util.ArrayList

/**
 * Created by vlad on 7/17/14.
 */

public abstract class FlowCommand : AbstractCommand() {

    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        val topFrameInfo = Parser.tryParseStoppedAt(output)
        if (topFrameInfo != null) {
            val breakpoint = debugProcess.getBreakpointAtLine(topFrameInfo.filePosition.startLine)!!
            debugProcess.debugger.history(breakpoint, topFrameInfo)
        }
    }
}
