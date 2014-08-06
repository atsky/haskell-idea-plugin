package org.jetbrains.haskell.debugger.utils

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import com.sun.org.apache.xpath.internal.operations.Bool
import java.util.concurrent.locks.ReentrantLock

/**
 *
 *
 * @author Habibullin Marat
 */
public class SyncObject() {
    private val lock: Lock = ReentrantLock()
    private val condition: Condition = lock.newCondition()
    private var conditionSignaled: Boolean = false

    public fun lock(): Unit = lock.lock()
    public fun unlock(): Unit = lock.unlock()
    public fun signaled(): Boolean = conditionSignaled
    public fun signal(): Unit {
        conditionSignaled = true
        condition.signal()
    }
    public fun await(): Unit = condition.await()
}