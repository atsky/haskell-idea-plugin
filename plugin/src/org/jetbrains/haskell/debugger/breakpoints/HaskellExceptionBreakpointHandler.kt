package org.jetbrains.haskell.debugger.breakpoints

import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XBreakpoint
import org.jetbrains.haskell.debugger.HaskellDebugProcess

/**
 * Created by vlad on 8/6/14.
 */

public class HaskellExceptionBreakpointHandler(val debugProcess: HaskellDebugProcess) :
        XBreakpointHandler<XBreakpoint<HaskellExceptionBreakpointProperties>>(HaskellExceptionBreakpointType::class.java) {

    override fun registerBreakpoint(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        debugProcess.addExceptionBreakpoint(breakpoint)
    }

    override fun unregisterBreakpoint(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>, temporary: Boolean) {
        debugProcess.removeExceptionBreakpoint(breakpoint)
    }

}