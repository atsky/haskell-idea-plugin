package org.jetbrains.haskell.debugger.frames

import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.frame.XExecutionStack
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger

public class HsSuspendContext(val debugger: ProcessDebugger,
                              val threadInfo: ProgramThreadInfo) : XSuspendContext() {
    private val _activeExecutionStack: XExecutionStack = HsExecutionStack(debugger, threadInfo)
    override fun getActiveExecutionStack(): XExecutionStack = _activeExecutionStack

    /**
     * This method is not overrode, default implementation returns array of one element - _activeExecutionStack
     */
    //    override fun getExecutionStacks(): Array<XExecutionStack>?
}