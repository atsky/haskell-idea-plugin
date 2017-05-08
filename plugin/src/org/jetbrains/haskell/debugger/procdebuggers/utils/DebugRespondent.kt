package org.jetbrains.haskell.debugger.procdebuggers.utils

import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.parser.HistoryResult
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointDescription


interface DebugRespondent {

    fun traceFinished()

    fun positionReached(context: HsSuspendContext)

    fun breakpointReached(breakpoint: HaskellLineBreakpointDescription,
                                 context: HsSuspendContext)

    fun exceptionReached(context: HsSuspendContext)

    fun breakpointRemoved()

    fun getBreakpointAt(module: String, line: Int): HaskellLineBreakpointDescription?

    fun setBreakpointNumberAt(breakpointNumber: Int, module: String, line: Int)

    fun resetHistoryStack()

    fun historyChange(currentFrame: HsHistoryFrame, history: HistoryResult?)

    fun getModuleByFile(filename: String): String

}