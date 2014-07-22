package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import com.intellij.xdebugger.frame.XExecutionStack
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import com.intellij.xdebugger.frame.XStackFrame
import org.jetbrains.haskell.debugger.frames.HsStackFrameInfo
import java.util.LinkedList
import java.util.Collections
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import org.jetbrains.haskell.debugger.utils.HaskellUtils

public class HsExecutionStack(private val threadInfo: ProgramThreadInfo?) : XExecutionStack(threadInfo!!.name) {

    private var topFrame: HsStackFrame? = null

    override fun getTopFrame(): XStackFrame? {
        if (topFrame == null) {
            val allFrames = threadInfo!!.frames
            if (allFrames != null) {
                topFrame = HsStackFrame(allFrames.get(0))
            }
        }
        return topFrame
    }

    override fun computeStackFrames(firstFrameIndex: Int, container: XExecutionStack.XStackFrameContainer?) {
        if(container != null) {
            val allFrames = threadInfo!!.frames
            if (allFrames != null && firstFrameIndex < allFrames.size()) {
                val xFrames = LinkedList<HsStackFrame>()
                for (i in firstFrameIndex .. allFrames.size() - 1) {
                    xFrames.add(HsStackFrame(allFrames.get(i)))
                }
                container.addStackFrames(xFrames, true)
            } else {
                container.addStackFrames(Collections.emptyList<XStackFrame>(), true)
            }
        }
    }
}