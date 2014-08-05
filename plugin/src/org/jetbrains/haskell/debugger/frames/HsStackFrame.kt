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
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import com.intellij.ui.ColoredTextContainer
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.XDebuggerBundle

public abstract class HsStackFrame(protected val debugProcess: HaskellDebugProcess,
                                   public val filePosition: HsFilePosition,
                                   bindings: ArrayList<LocalBinding>?) : XStackFrame() {
    class object {
        private val STACK_FRAME_EQUALITY_OBJECT = Object()
    }

    public var obsolete: Boolean = true

    override fun getEqualityObject(): Any? = STACK_FRAME_EQUALITY_OBJECT

    protected var bindingsList: XValueChildrenList? = null;
    {
        setBindingsList(bindings)
    }

    protected fun setBindingsList(bindings: ArrayList<LocalBinding>?) {
        if(bindings != null) {
            bindingsList = XValueChildrenList()
            for (binding in bindings) {
                bindingsList?.add(HsDebugValue(binding))
            }
        }
    }

    /**
     * This method always returns null because we need to switch off default debug highlighting provided by
     * ExecutionPointHighlighter inside XDebuggerManagerImpl. Custom highlighting provided by
     * HsExecutionPointHighlighter inside HsDebugSessionListener. Also, see hackSourcePosition property below
     *
     * @see com.intellij.xdebugger.impl.XDebuggerManagerImpl
     */
    override fun getSourcePosition(): XSourcePosition? = null

    /**
     * This property holds XSourcePosition value. Use it instead of getSourcePosition()
     */
    public val hackSourcePosition: XSourcePosition? = XDebuggerUtil.getInstance()!!.createPosition(
                        LocalFileSystem.getInstance()?.findFileByIoFile(File(filePosition.filePath)),
                        filePosition.normalizedStartLine)

    /**
     * Returns evaluator (to use 'Evaluate expression' and other such tools)
     */
    override fun getEvaluator(): XDebuggerEvaluator? = HsDebuggerEvaluator(debugProcess.debugger)

    /**
     * Makes stack frame appearance customization in frames list. Sets function name, source file name and part of code
     * (span) that this frame represents
     */
    override fun customizePresentation(component: ColoredTextContainer) {
        val position = hackSourcePosition
        if (position != null) {
            component.append(position.getFile().getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            setSourceSpan(component)
            component.setIcon(AllIcons.Debugger.StackFrame);
        } else {
            component.append(XDebuggerBundle.message("invalid.frame") ?: "<invalid frame>",
                                                     SimpleTextAttributes.ERROR_ATTRIBUTES);
        }
    }

    /**
     * Creates HsDebugValue instances for local bindings in stackFrameInfo.bindings and adds them in passed node. These
     * added HsDebugValue instances are shown in 'Variables' panel of 'Debug' tool window.
     */
    override fun computeChildren(node: XCompositeNode) {
        if (node.isObsolete()) {
            return
        }
        ApplicationManager.getApplication()!!.executeOnPooledThread(object : Runnable {
            override fun run() {
                try {
                    if(bindingsList == null || obsolete) {
                        tryGetBindings()
                    }
                    if(bindingsList != null) {
                        node.addChildren(bindingsList as XValueChildrenList, true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    node.setErrorMessage("Unable to display frame variables")
                }

            }
        })
    }

    protected abstract fun tryGetBindings()

    /**
     * Sets the bounds of code in source file this frame represents. Format is similar to one in ghci:
     * one line span: "<line number> : <start symbol number> - <end symbol number>"
     * multiline span: "(<start line number>,<start symbol number>) - (<end line number>,<end symbol number>)"
     */
    private fun setSourceSpan(component: ColoredTextContainer) {
        val srcSpan: String
        if (filePosition.rawStartLine != filePosition.rawEndLine) {
            srcSpan = ":(" + filePosition.rawStartLine + "," + filePosition.rawStartSymbol + ")-(" +
                    filePosition.rawEndLine + "," + filePosition.normalizedEndSymbol + ")"
        } else {
            srcSpan = ":" + filePosition.rawStartLine +
                    ":" + filePosition.rawStartSymbol + "-" + filePosition.normalizedEndSymbol
        }
        component.append(srcSpan, SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }
}
