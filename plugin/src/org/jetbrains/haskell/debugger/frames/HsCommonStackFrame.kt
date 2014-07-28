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
import org.jetbrains.haskell.debugger.parser.HsCommonStackFrameInfo
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes

/**
 * Created by marat-x on 7/22/14.
 */
public class HsCommonStackFrame(debugProcess: HaskellDebugProcess,
                                private val indexInHist: Int,
                                private val allHistFramesArray: ArrayList<HsCommonStackFrameInfo>)
                              : HsStackFrame(debugProcess, allHistFramesArray.get(indexInHist).filePosition) {

    private val thisStackFrameInfo = allHistFramesArray.get(indexInHist)

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
                    if(thisStackFrameInfo.bindings == null) {
                        tryGetBindings()
                    }
                    if(thisStackFrameInfo.bindings != null) {
                        setChildrenToNode(node, thisStackFrameInfo.bindings as ArrayList<LocalBinding>)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    node.setErrorMessage("Unable to display frame variables")
                }

            }
        })
    }

    override fun customizePresentation(component: ColoredTextContainer) {
        component.append(thisStackFrameInfo.functionName + " : ", SimpleTextAttributes.REGULAR_ATTRIBUTES);
        super<HsStackFrame>.customizePresentation(component)
    }

    private fun tryGetBindings() {
        val syncObject: Lock = ReentrantLock()
        val bindingsAreSet: Condition = syncObject.newCondition()
        syncObject.lock()
        try {
            debugProcess.fillFramesFromHistory(allHistFramesArray, syncObject, bindingsAreSet, thisStackFrameInfo.index)
            while (thisStackFrameInfo.bindings == null) {
                bindingsAreSet.await()
            }
        } finally {
            syncObject.unlock()
        }
    }
}