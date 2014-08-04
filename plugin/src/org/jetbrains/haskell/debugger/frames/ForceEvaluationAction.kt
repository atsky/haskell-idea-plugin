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
 * Determines action performed when user select 'Force evaluation' in context menu of variable in current frame.
 * This action is called by IDEA, so class is registered in plugin.xml
 *
 * @author Habibullin Marat
 */
public class ForceEvaluationAction(): XDebuggerTreeActionBase() {
    override fun perform(node: XValueNodeImpl?, nodeName: String, actionEvent: AnActionEvent?) {
        if(node == null || actionEvent == null) {
            return
        }
        val debugProcess = tryGetDebugProcess(actionEvent)
        if(debugProcess == null) {
            return
        }
        forceSetValue(node, debugProcess)
    }

    private fun tryGetDebugProcess(actionEvent: AnActionEvent): HaskellDebugProcess? {
        val project = actionEvent.getProject()
        if(project == null) {
            return null
        }
        val debuggerManager = XDebuggerManager.getInstance(project)
        if(debuggerManager == null) {
            return null
        }
        val session = debuggerManager.getCurrentSession()
        if(session == null) {
            return null
        }
        return session.getDebugProcess() as HaskellDebugProcess
    }

    private fun forceSetValue(node: XValueNodeImpl, debugProcess: HaskellDebugProcess) {
        val hsDebugValue = node.getValueContainer() as HsDebugValue
        debugProcess.forceSetValue(hsDebugValue.binding)
        if(hsDebugValue.binding.value != null) {
            node.setPresentation(null, XRegularValuePresentation(hsDebugValue.binding.value as String, hsDebugValue.binding.typeName), false)
        }
    }
}