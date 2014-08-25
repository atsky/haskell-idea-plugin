package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger

/**
 * Created by vlad on 8/5/14.
 */

public class HsHistoryFrame(debugger: ProcessDebugger, stackFrameInfo: HsStackFrameInfo) :
        HsStackFrame(debugger, stackFrameInfo) {

    override fun tryGetBindings() {
        if (stackFrameInfo.bindings == null) {
            return
        }
        val syncObject: Lock = ReentrantLock()
        val bindingsAreSet: Condition = syncObject.newCondition()
        syncObject.lock()
        try {
            for (bind in stackFrameInfo.bindings!!) {
                debugger.updateBinding(bind, syncObject, bindingsAreSet)
                bindingsAreSet.await()
            }
        } finally {
            syncObject.unlock()
        }
        setBindingsList(stackFrameInfo.bindings)
    }

}