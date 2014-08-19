package org.jetbrains.haskell.debugger.procdebuggers.utils

import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock
import org.jetbrains.haskell.debugger.parser.ParseResult
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by vlad on 7/15/14.
 */

public class CommandQueue(val execute: (AbstractCommand<out ParseResult?>) -> Unit) : Runnable {

    private val highPriorityCommands = LinkedList<AbstractCommand<out ParseResult?>>()
    private val lowPriorityCommands = LinkedList<AbstractCommand<out ParseResult?>>()
    private var running = true

    private val inputLock = ReentrantLock()
    private var ready: Boolean = false
    private val readyCondition = inputLock.newCondition()

    private val someCommandInProgress: AtomicBoolean = AtomicBoolean(false)
    public fun someCommandInProgress(): Boolean = someCommandInProgress.get()

    override fun run() {
        while (running) {
            inputLock.lock()
            while (running && (lowPriorityCommands.empty && highPriorityCommands.empty || !ready)) {
                readyCondition.await()
            }
            var command: AbstractCommand<out ParseResult?>? = null
            if (running) {
                command = if (!highPriorityCommands.empty) highPriorityCommands.removeFirst() else lowPriorityCommands.removeFirst()
                ready = false
            }
            inputLock.unlock()

            if (command != null) {
                execute(command!!)
                someCommandInProgress.set(true)
            }
        }
    }

    /**
     * Adds new command to the queue.
     * @param highPriority should be set to true in another command's callback, so that the sequence of commands could be executed at once
     */
    public fun addCommand(command: AbstractCommand<out ParseResult?>, highPriority: Boolean = false) {
        inputLock.lock()
        if (highPriority) {
            highPriorityCommands.addLast(command)
        } else {
            lowPriorityCommands.addLast(command)
        }
        readyCondition.signal()
        inputLock.unlock()
    }

    public fun start() {
        Thread(this).start()
    }

    public fun stop() {
        inputLock.lock()
        running = false
        readyCondition.signal()
        someCommandInProgress.set(false)
        inputLock.unlock()
    }

    public fun setReadyForInput() {
        inputLock.lock()
        ready = true
        readyCondition.signal()
        someCommandInProgress.set(false)
        inputLock.unlock()
    }

}
