package org.jetbrains.haskell.debugger.breakpoints

import com.intellij.openapi.extensions.ExtensionPointName
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpoint

public interface HaskellBreakpointHandlerFactory {
    public fun createBreakpointHandler(process: HaskellDebugProcess): XBreakpointHandler<XBreakpoint<*>>
}