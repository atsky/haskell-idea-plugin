package org.jetbrains.haskell.debugger.procdebuggers.utils

import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.parser.HistoryResult
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointDescription


public interface DebugRespondent {

    public fun traceFinished()

    public fun positionReached(context: HsSuspendContext)

    public fun breakpointReached(breakpoint: HaskellLineBreakpointDescription,
                                 context: HsSuspendContext)

    public fun exceptionReached(context: HsSuspendContext)

    public fun breakpointRemoved()

    public fun getBreakpointAt(module: String, line: Int): HaskellLineBreakpointDescription?

    public fun setBreakpointNumberAt(breakpointNumber: Int, module: String, line: Int)

    public fun resetHistoryStack()

    public fun historyChange(currentFrame: HsHistoryFrame, history: HistoryResult?)

    public fun getModuleByFile(filename: String): String

}