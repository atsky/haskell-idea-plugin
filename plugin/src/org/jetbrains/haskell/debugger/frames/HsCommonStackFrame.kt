package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import com.intellij.xdebugger.XSourcePosition
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.LocalBinding
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.xdebugger.frame.XValueChildrenList
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.parser.HsGeneralStackFrameInfo

/**
 * Created by marat-x on 7/22/14.
 */
public class HsCommonStackFrame(debugProcess: HaskellDebugProcess,
                                 private val stackFrameInfo: HsGeneralStackFrameInfo?)
                               : HsStackFrame(debugProcess, stackFrameInfo?.filePosition) {

    public fun setBindings(newBindings: ArrayList<LocalBinding>?): Unit {
        if(stackFrameInfo != null) {
            if(newBindings == null) {
                stackFrameInfo.bindings = null
            } else {
                stackFrameInfo.bindings = ArrayList<LocalBinding>()
                stackFrameInfo.bindings?.addAll(newBindings)
            }
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
                    if(stackFrameInfo != null) {
                        if(stackFrameInfo.bindings == null) {
                            tryGetBindings()
                        }
                        if(stackFrameInfo.bindings != null) {
                            setChildrenToNode(node, stackFrameInfo.bindings as ArrayList<LocalBinding>)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    node.setErrorMessage("Unable to display frame variables")
                }

            }
        })
    }

    private fun tryGetBindings() {
        if(stackFrameInfo != null) {
            val syncObject: Lock = ReentrantLock()
            val bindingsAreSet: Condition = syncObject.newCondition()
            syncObject.lock()
            try {
                debugProcess.fillFrameFromHistory(this, syncObject, bindingsAreSet, stackFrameInfo.index)
                while (stackFrameInfo.bindings == null) {
                    bindingsAreSet.await()
                }
            } finally {
                syncObject.unlock()
            }
        }
    }
}