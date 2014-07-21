package org.jetbrains.haskell.debugger.breakpoints

import com.intellij.openapi.extensions.ExtensionPointName
import org.jetbrains.haskell.debugger.GHCiDebugProcess
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpoint

public trait HaskellBreakpointHandlerFactory {
    public fun createBreakpointHandler(process: GHCiDebugProcess): XBreakpointHandler<XBreakpoint<*>>
}