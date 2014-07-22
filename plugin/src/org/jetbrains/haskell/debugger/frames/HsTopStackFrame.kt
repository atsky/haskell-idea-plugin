package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.openapi.application.ApplicationManager
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.LocalBinding
import com.intellij.xdebugger.frame.XValueChildrenList

/**
 * Created by marat-x on 7/22/14.
 */
public class HsTopStackFrame(debugProcess: HaskellDebugProcess,
                             private val stackFrameInfo: HsTopStackFrameInfo?)
                           : HsStackFrame(debugProcess, stackFrameInfo?.filePosition) {

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
                    if(stackFrameInfo != null && stackFrameInfo.bindings != null) {
                        setChildrenToNode(node, stackFrameInfo.bindings as ArrayList<LocalBinding>)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    node.setErrorMessage("Unable to display frame variables")
                }

            }
        })
    }
}