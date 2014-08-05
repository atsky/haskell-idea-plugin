package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition

/**
 * Created by vlad on 8/5/14.
 */

public class HsHistoryFrame(debugProcess: HaskellDebugProcess, val stackFrameInfo: HsStackFrameInfo) :
        HsStackFrame(debugProcess, stackFrameInfo.filePosition, stackFrameInfo.bindings) {

    override fun tryGetBindings() {
        if (stackFrameInfo.bindings == null) {
            return
        }
        for (bind in stackFrameInfo.bindings!!) {
            if (bind.typeName == null) {

            }
        }
        val syncObject: Lock = ReentrantLock()
        val bindingsAreSet: Condition = syncObject.newCondition()
        syncObject.lock()
        try {
            while (stackFrameInfo.bindings == null) {
                bindingsAreSet.await()
            }
        } finally {
            syncObject.unlock()
        }
        setBindingsList(stackFrameInfo.bindings)
        obsolete = false
    }

}