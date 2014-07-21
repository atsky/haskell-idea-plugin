package org.jetbrains.haskell.debugger.breakpoints

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.utils.HaskellUtils

public class HaskellLineBreakpointHandler(breakpointTypeClass : Class<out XBreakpointType<XLineBreakpoint<XBreakpointProperties<*>>, *>>,
                                          val debugProcess: HaskellDebugProcess)
                                        : XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(breakpointTypeClass)
{
    /**
     * Called when new breakpoint is added
     *
     * @param breakpoint added breakpoint
     */
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        val breakpointLineNumber : Int? = getHaskellBreakpointLineNumber(breakpoint)
        if(breakpointLineNumber != null) {
            debugProcess.addBreakpoint(breakpointLineNumber, breakpoint)
        }
    }

    /**
     * Called when breakpoint is removed
     *
     * @param breakpoint breakpoint to remove
     */
    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, isTemporary: Boolean) {
        val breakpointLineNumber : Int? = getHaskellBreakpointLineNumber(breakpoint)
        if(breakpointLineNumber != null) {
            debugProcess.removeBreakpoint(breakpointLineNumber)
        }
    }

    private fun getHaskellBreakpointLineNumber(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>): Int? {
        val lineNum = breakpoint.getSourcePosition()?.getLine()
        if(lineNum != null) {
            return HaskellUtils.zeroBasedToHaskellLineNumber(lineNum)
        }
        return null
    }
}