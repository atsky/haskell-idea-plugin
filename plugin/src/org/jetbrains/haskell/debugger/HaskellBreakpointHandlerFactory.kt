package org.jetbrains.haskell.debugger

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpoint

/**
 * @author Habibullin Marat
 */
public trait HaskellBreakpointHandlerFactory {
    class object {
        public val EXTENSION_POINT_NAME: ExtensionPointName<HaskellBreakpointHandlerFactory>? = ExtensionPointName.create("Haskell.breakpointHandler")
    }

    public fun createBreakpointHandler(process: GHCiDebugProcess): XBreakpointHandler<XBreakpoint<*>>
}