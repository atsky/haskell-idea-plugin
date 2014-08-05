package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import com.intellij.xdebugger.frame.XExecutionStack
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import com.intellij.xdebugger.frame.XStackFrame
import java.util.LinkedList
import java.util.Collections
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.ArrayList

/**
 * @author Habibullin Marat
 * @see    XExecutionStack
 */
public class HsExecutionStack(private val debugProcess: HaskellDebugProcess,
                              private val threadInfo: ProgramThreadInfo) : XExecutionStack(threadInfo.name) {
    private val topFrame: HsStackFrame? = HsTopStackFrame(debugProcess, threadInfo.topFrameInfo)
    override fun getTopFrame(): XStackFrame? = topFrame

    override fun computeStackFrames(firstFrameIndex: Int, container: XExecutionStack.XStackFrameContainer?) {
    }
}