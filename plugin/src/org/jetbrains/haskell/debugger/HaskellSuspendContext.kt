package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.frame.XExecutionStack

/**
 * @author Habibullin Marat
 */
public class HaskellSuspendContext(threadInfo: ProgramThreadInfo) : XSuspendContext() {
    private val _activeExecutionStack : XExecutionStack = HaskellExecutionStack(threadInfo)
    override fun getActiveExecutionStack(): XExecutionStack? = _activeExecutionStack

    /**
     * This method is not overrode, default implementation returns array of one element - _activeExecutionStack
     */
//    override fun getExecutionStacks(): Array<XExecutionStack>?
}