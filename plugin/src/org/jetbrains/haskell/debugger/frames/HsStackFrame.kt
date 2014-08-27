package org.jetbrains.haskell.debugger.frames

import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.ui.SimpleTextAttributes
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.ArrayList
import com.intellij.ui.ColoredTextContainer
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.XDebuggerBundle
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger

public abstract class HsStackFrame(val debugger: ProcessDebugger,
                                   public val stackFrameInfo: HsStackFrameInfo) : XStackFrame() {
    class object {
        private val STACK_FRAME_EQUALITY_OBJECT = Object()
    }

    public var obsolete: Boolean = true

    override fun getEqualityObject(): Any? = STACK_FRAME_EQUALITY_OBJECT

    protected var bindingsList: XValueChildrenList? = null;
    {
        setBindingsList(stackFrameInfo.bindings)
    }

    protected fun setBindingsList(bindings: ArrayList<LocalBinding>?) {
        if (bindings != null) {
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

    private var _sourcePositionSet: Boolean = false
    private var _hackSourcePosition: XSourcePosition? = null
    /**
     * This property holds XSourcePosition value. Use it instead of getSourcePosition()
     */
    public val hackSourcePosition: XSourcePosition?
        get() {
            if (!_sourcePositionSet) {
                _hackSourcePosition = if (stackFrameInfo.filePosition == null) null else
                    XDebuggerUtil.getInstance()!!.createPosition(
                            LocalFileSystem.getInstance()?.findFileByIoFile(File(stackFrameInfo.filePosition!!.filePath)),
                            stackFrameInfo.filePosition!!.normalizedStartLine)
                _sourcePositionSet = true
            }
            return _hackSourcePosition
        }

    /**
     * Returns evaluator (to use 'Evaluate expression' and other such tools)
     */
    override fun getEvaluator(): XDebuggerEvaluator? = HsDebuggerEvaluator(debugger)

    /**
     * Makes stack frame appearance customization in frames list. Sets function name, source file name and part of code
     * (span) that this frame represents
     */
    override fun customizePresentation(component: ColoredTextContainer) {
        val position = hackSourcePosition
        if (position != null) {
            if (stackFrameInfo.functionName != null) {
                component.append(stackFrameInfo.functionName as String, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
                component.append(" (", SimpleTextAttributes.REGULAR_ATTRIBUTES)
            }
            component.append(position.getFile().getName() + ":", SimpleTextAttributes.REGULAR_ATTRIBUTES)
            setSourceSpan(component)
            if (stackFrameInfo.functionName != null) {
                component.append(")", SimpleTextAttributes.REGULAR_ATTRIBUTES)
            }
            component.setIcon(AllIcons.Debugger.StackFrame)
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
                    if (bindingsList == null || obsolete) {
                        tryGetBindings()
                        obsolete = false
                    }
                    if (bindingsList != null) {
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
     * Sets the bounds of code in source file this frame represents"
     */
    private fun setSourceSpan(component: ColoredTextContainer) {
        val srcSpan: String
        if (stackFrameInfo.filePosition != null) {
            srcSpan = stackFrameInfo.filePosition!!.spanToString()
        } else {
            srcSpan = "<exception thrown>"
        }
        component.append(srcSpan, SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }
}
