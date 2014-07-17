package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.XSourcePosition

/**
 * Class is used to handle breakpoint registration events, i.e. when you add / remove breakpoints on debug process is running
 * or when debug process starts having set breakpoints, methods of this class are called to perform appropriate actions
 *
 * @author Habibullin Marat
 */
public class HaskellLineBreakpointHandler(breakpointTypeClass : Class<out XBreakpointType<XLineBreakpoint<XBreakpointProperties<*>>, *>>,
                                          val debugProcess: GHCiDebugProcess)
                                        : XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(breakpointTypeClass)
{
//    private val breakpointPositions : MutableMap<XLineBreakpoint<XBreakpointProperties<*>>, XSourcePosition> = hashMapOf()

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