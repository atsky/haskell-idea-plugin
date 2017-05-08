package org.jetbrains.haskell.debugger.frames

import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame
import java.util.LinkedList
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger

/**
 * @author Habibullin Marat
 * @see    XExecutionStack
 */
class HsExecutionStack(private val debugger: ProcessDebugger,
                              private val threadInfo: ProgramThreadInfo) : XExecutionStack(threadInfo.name) {
    private val topFrame: HsStackFrame? = HsTopStackFrame(debugger, threadInfo.topFrameInfo)
    override fun getTopFrame(): XStackFrame? = topFrame

    override fun computeStackFrames(firstFrameIndex: Int, container: XExecutionStack.XStackFrameContainer?) {
        container?.addStackFrames(LinkedList<XStackFrame>(), true)
    }
}