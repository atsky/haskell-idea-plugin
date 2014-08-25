package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger

/**
 * Created by marat-x on 7/22/14.
 */
public class HsTopStackFrame(debugger: ProcessDebugger,
                             stackFrameInfo: HsStackFrameInfo)
: HsStackFrame(debugger, stackFrameInfo) {

    override fun tryGetBindings() {
        // does nothing, because there is no way to get bindings if they are not set
    }
}