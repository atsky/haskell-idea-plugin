package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.frame.XExecutionStack
import org.jetbrains.haskell.debugger.HaskellDebugProcess

public class HsSuspendContext(private val debugProcess: HaskellDebugProcess,
                              val threadInfo: ProgramThreadInfo) : XSuspendContext() {
    private val _activeExecutionStack : XExecutionStack = HsExecutionStack(debugProcess, threadInfo)
    override fun getActiveExecutionStack(): XExecutionStack? = _activeExecutionStack

    /**
     * This method is not overrode, default implementation returns array of one element - _activeExecutionStack
     */
//    override fun getExecutionStacks(): Array<XExecutionStack>?
}