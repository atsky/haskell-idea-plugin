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
    private val breakpointPositions : MutableMap<XLineBreakpoint<XBreakpointProperties<*>>, XSourcePosition> = hashMapOf()

    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        val breakpointPos : XSourcePosition? = breakpoint.getSourcePosition()
        if(breakpointPos != null) {
            breakpointPositions.put(breakpoint, breakpointPos)
            //todo: implement position converting
            debugProcess.addBreakpoint(breakpointPos.getLine() + 1, breakpoint)
        }
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, isTemporary: Boolean) {
        val breakpointPos : XSourcePosition? = breakpointPositions.get(breakpoint)
        if (breakpointPos != null) {
            breakpointPositions.remove(breakpoint)
            //todo: implement position converting
            debugProcess.removeBreakpoint(breakpointPos.getLine() + 1)
        }
    }
}