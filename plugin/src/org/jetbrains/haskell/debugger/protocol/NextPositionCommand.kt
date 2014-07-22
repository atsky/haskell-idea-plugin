package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.frames.HsStackFrameInfo
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import java.util.ArrayList
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties

/**
 * @author Habibullin Marat
 */
public abstract class NextPositionCommand : AbstractCommand() {
    protected fun getCurrentFrame(output: Deque<String?>): HsStackFrameInfo? {
        return Parser.tryParseStoppedAt(output)
    }

    protected fun sendHistCommand(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?,
                                              topFrameInfo: HsStackFrameInfo,
                                              debugProcess: HaskellDebugProcess) {
        val singleFrameList = ArrayList<HsStackFrameInfo>()
        singleFrameList.add(topFrameInfo)
        debugProcess.debugger.history(breakpoint, topFrameInfo)
    }
}