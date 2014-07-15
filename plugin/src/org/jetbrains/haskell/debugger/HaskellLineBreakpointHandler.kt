package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.XSourcePosition

public class HaskellLineBreakpointHandler(breakpointTypeClass : Class<out XBreakpointType<XLineBreakpoint<XBreakpointProperties<*>>, *>>,
                                          val debugProcess: GHCiDebugProcess)
                                        : XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(breakpointTypeClass)
{
//    private val breakpointPositions : MutableMap<XLineBreakpoint<XBreakpointProperties<*>>, XSourcePosition> = hashMapOf()

    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        val breakpointLineNumber : Int? = getHaskellBreakpointLineNumber(breakpoint)
        if(breakpointLineNumber != null) {
            debugProcess.addBreakpoint(breakpointLineNumber, breakpoint)
        }
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, isTemporary: Boolean) {
        val breakpointLineNumber : Int? = getHaskellBreakpointLineNumber(breakpoint)
        if(breakpointLineNumber != null) {
            debugProcess.removeBreakpoint(breakpointLineNumber)
        }
    }

    private fun getHaskellBreakpointLineNumber(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>): Int? =
            breakpoint.getSourcePosition()?.getLine()?.plus(1)
}