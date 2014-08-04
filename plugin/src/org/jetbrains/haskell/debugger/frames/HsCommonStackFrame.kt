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
: HsStackFrame(debugProcess, allHistFramesArray.get(indexInHist).filePosition, allHistFramesArray.get(indexInHist).bindings) {

    private val thisStackFrameInfo = allHistFramesArray.get(indexInHist)

    override fun customizePresentation(component: ColoredTextContainer) {
        component.append(thisStackFrameInfo.functionName + " : ", SimpleTextAttributes.REGULAR_ATTRIBUTES);
        super<HsStackFrame>.customizePresentation(component)
    }

    override fun tryGetBindings() {
        if(thisStackFrameInfo.bindings == null) {
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
        setBindingsList(thisStackFrameInfo.bindings)
    }
}