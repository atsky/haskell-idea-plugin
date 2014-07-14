package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointType

public class HaskellLineBreakpointHandler(breakpointTypeClass : Class<out XBreakpointType<XLineBreakpoint<XBreakpointProperties<*>>, *>>,
                                          val debugProcess: GHCiDebugProcess)
                                        : XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(breakpointTypeClass)
{
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        // unimplemented yet
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, isTemporary: Boolean) {
        // unimplemented yet
    }
}