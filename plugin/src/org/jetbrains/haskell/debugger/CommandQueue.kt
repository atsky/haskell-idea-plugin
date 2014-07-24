package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by vlad on 7/15/14.
 */

public class CommandQueue(val execute: (AbstractCommand) -> Unit) : Runnable {

    private val highPriorityCommands = LinkedList<AbstractCommand>()
    private val lowPriorityCommands = LinkedList<AbstractCommand>()
    private var running = true

    private val inputLock = ReentrantLock()
    private var ready: Boolean = false
    private val readyCondition = inputLock.newCondition()

    override fun run() {
        while (running) {
            inputLock.lock()
            while (running && (lowPriorityCommands.empty && highPriorityCommands.empty || !ready)) {
                readyCondition.await()
            }
            var command: AbstractCommand? = null
            if (running) {
                command = if (!highPriorityCommands.empty) highPriorityCommands.removeFirst() else lowPriorityCommands.removeFirst()
                ready = false
            }
            inputLock.unlock()

            if (command != null) {
                execute(command!!)
            }
        }
    }

    /**
     * Adds new command to the queue.
     * @param highPriority should be set to true in another command's callback, so that the sequence of commands could be executed at once
     */
    public fun addCommand(command: AbstractCommand, highPriority: Boolean = false) {
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
        inputLock.unlock()
    }

    public fun setReadyForInput() {
        inputLock.lock()
        ready = true
        readyCondition.signal()
        inputLock.unlock()
    }

}