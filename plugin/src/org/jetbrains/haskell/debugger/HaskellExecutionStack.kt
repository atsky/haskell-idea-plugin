package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XExecutionStack.XStackFrameContainer
import java.util.Collections
import java.util.LinkedList
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File

/**
 * @author Habibullin Marat
 */

public class HaskellExecutionStack(private val threadInfo: ProgramThreadInfo?) : XExecutionStack(threadInfo!!.name) {

    private var topFrame: HaskellStackFrame? = null

    override fun getTopFrame(): XStackFrame? {
        if (topFrame == null) {
            val allFrames = threadInfo!!.frames
            if (allFrames != null) {
                topFrame = createFrame(allFrames.get(0))
            }
        }
        return topFrame
    }

    override fun computeStackFrames(firstFrameIndex: Int, container: XStackFrameContainer?) {
        if(container != null) {
//            if (threadInfo!!.state != ProgramThreadInfo.State.SUSPENDED) {
//                container.errorOccurred("Frames not available in non-suspended state")
//                return
//            }

            val allFrames = threadInfo!!.frames
            if (allFrames != null && firstFrameIndex < allFrames.size()) {
                val xFrames = LinkedList<HaskellStackFrame>()
                for (i in firstFrameIndex .. allFrames.size() - 1) {
                    xFrames.add(createFrame(allFrames.get(i)))
                }
                container.addStackFrames(xFrames, true)
            } else {
                container.addStackFrames(Collections.emptyList<XStackFrame>(), true)
            }
        }
    }

    private fun createFrame(frameInfo: HaskellStackFrameInfo): HaskellStackFrame {
        return HaskellStackFrame(XDebuggerUtil.getInstance()!!.createPosition(
                LocalFileSystem.getInstance()?.findFileByIoFile(File(frameInfo.filePath)),
                HaskellUtils.haskellLineNumberToZeroBased(frameInfo.startLine)))
    }
}