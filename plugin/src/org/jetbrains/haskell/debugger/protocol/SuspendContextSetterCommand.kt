package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.frames.HsStackFrameInfo
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.ArrayList

/**
 * @author Habibullin Marat
 */
public abstract class SuspendContextSetterCommand(): RealTimeCommand() {
    protected fun setSuspendContext(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?,
                                    frames: ArrayList<HsStackFrameInfo>,
                                    debugProcess: HaskellDebugProcess) {
        val context = HsSuspendContext(ProgramThreadInfo(null, "Main", frames))
        if (breakpoint != null) {
            debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
        } else {
            debugProcess.getSession()!!.positionReached(context)
        }
    }
}