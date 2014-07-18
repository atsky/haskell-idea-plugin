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

public class HaskellStackFrame(private val positionInSource: XSourcePosition?) : XStackFrame() {

    class object {
        private val STACK_FRAME_EQUALITY_OBJECT = Object()

        private fun gray(attributes: SimpleTextAttributes, gray: Boolean): SimpleTextAttributes {
            if (!gray) {
                return attributes
            } else {
                return if ((attributes.getStyle() and SimpleTextAttributes.STYLE_ITALIC) != 0)
                    SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES
                else
                    SimpleTextAttributes.GRAYED_ATTRIBUTES
            }
        }
    }

    override fun getEqualityObject(): Any? = STACK_FRAME_EQUALITY_OBJECT

    override fun getSourcePosition(): XSourcePosition? = positionInSource

    /**
     * This method should return evaluator (to use 'Evaluate expression' and other such tools) but this functionality
     * is not supported yet
     */
    override fun getEvaluator(): XDebuggerEvaluator? = null

    /**
     * Stack frame appearance customization, not implemented yet, default implementation is used
     */
    //    override fun customizePresentation(component: ColoredTextContainer)

    /**
     * This method should compute local variables and other frame data to show in 'Variables' panel of 'Debug' tool window.
     * So we need to get ghci output, parse it, convert to XValueChildrenList and pass to node.addChildren() method
     */
    override fun computeChildren(node: XCompositeNode) {
        if (node.isObsolete()) {
            return
        }
        ApplicationManager.getApplication()!!.executeOnPooledThread(object : Runnable {
            override fun run() {
                try {
                    val list = XValueChildrenList()
                    list.add(createVariable("ten", "10"))
                    node.addChildren(list, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    node.setErrorMessage("Unable to display frame variables")
                }

            }
        })
    }

    private fun createVariable(name: String, value: String): VariableView {
        return VariableView(VariableImpl(name, PrimitiveValue(ValueType.STRING, value)),
                object : VariableContextBase() {
                    override fun getEvaluateContext(): EvaluateContext {
                        throw UnsupportedOperationException()
                    }
                    override fun watchableAsEvaluationExpression(): Boolean {
                        return false
                    }
                    override fun getDebugProcess(): DebuggerViewSupport {
                        return DebuggerViewSupport.SimpleDebuggerViewSupport()
                    }

                })
    }
}