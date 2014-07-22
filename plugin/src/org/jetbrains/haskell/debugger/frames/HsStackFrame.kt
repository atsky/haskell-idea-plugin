package org.jetbrains.haskell.debugger.frames

import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.ui.SimpleTextAttributes
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XCompositeNode
import org.jetbrains.debugger.VariableView
import org.jetbrains.debugger.VariableContextBase
import org.jetbrains.debugger.EvaluateContext
import org.jetbrains.debugger.DebuggerViewSupport
import org.jetbrains.debugger.values.PrimitiveValue
import com.intellij.openapi.application.ApplicationManager
import com.intellij.xdebugger.frame.XValueChildrenList
import org.jetbrains.debugger.VariableImpl
import org.jetbrains.debugger.values.ValueType
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.parser.FilePosition

public abstract class HsStackFrame(protected val debugProcess: HaskellDebugProcess,
                                   filePosition: FilePosition?) : XStackFrame() {
    class object {
        private val STACK_FRAME_EQUALITY_OBJECT = Object()
    }
    override fun getEqualityObject(): Any? = STACK_FRAME_EQUALITY_OBJECT

    private val _sourcePosition =
            if(filePosition != null) {
                XDebuggerUtil.getInstance()!!.createPosition(
                        LocalFileSystem.getInstance()?.findFileByIoFile(File(filePosition.filePath)),
                        HaskellUtils.haskellLineNumberToZeroBased(filePosition.startLine))
            }
            else null
    override fun getSourcePosition(): XSourcePosition? = _sourcePosition

    /**
     * This method should return evaluator (to use 'Evaluate expression' and other such tools) but this functionality
     * is not supported yet
     */
    override fun getEvaluator(): XDebuggerEvaluator? = null

    /**
     * Stack frame appearance customization, not implemented yet, default implementation is used
     */
    //    override fun customizePresentation(component: ColoredTextContainer)

    protected fun setChildrenToNode(node: XCompositeNode, bindings: ArrayList<LocalBinding>) {
        val list = XValueChildrenList()
        for (binding in bindings) {
            list.add(HsDebugValue(binding))
        }
        node.addChildren(list, true)
    }
}
