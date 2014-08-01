package org.jetbrains.haskell.debugger.frames

import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation
import com.intellij.openapi.actionSystem.LangDataKeys
import org.jetbrains.haskell.debugger.parser.LocalBinding
import com.intellij.xdebugger.XDebuggerManager
import org.jetbrains.haskell.debugger.HaskellDebugProcess

/**
 * @author Habibullin Marat
 */
public class ForceEvaluationAction(): XDebuggerTreeActionBase() {
    override fun perform(node: XValueNodeImpl?, nodeName: String, e: AnActionEvent?) {
        if(node == null || e == null) {
            return
        }
        val project = e.getProject()
        if(project == null) {
            return
        }
        val debuggerManager = XDebuggerManager.getInstance(project)
        if(debuggerManager == null) {
            return
        }
        val session = debuggerManager.getCurrentSession()
        if(session == null) {
            return
        }
        val debugProcess = session.getDebugProcess() as HaskellDebugProcess
        forceEvaluation(node, debugProcess)
        println((node.getValueContainer() as HsDebugValue).binding.name + " = " + (node.getValueContainer() as HsDebugValue).binding.value)
//      node?.setPresentation(null, XRegularValuePresentation("v", "t"), false)
    }

    private fun forceEvaluation(node: XValueNodeImpl, debugProcess: HaskellDebugProcess) {
        val hsDebugValue = node.getValueContainer() as HsDebugValue
        debugProcess.forceSetValue(hsDebugValue.binding)
    }
}