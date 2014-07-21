package org.jetbrains.haskell.debugger

import java.util.Queue
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import com.sun.jmx.remote.internal.ArrayQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock
import java.util.Collections

/**
 * Created by vlad on 7/15/14.
 */

public class CommandQueue(val debugger: GHCiDebugger) : Runnable {

    private val commands = LinkedList<AbstractCommand>()
    private var running = true

    private val inputLock = ReentrantLock()
    private var ready: Boolean = false
    private val readyCondition = inputLock.newCondition()

    override fun run() {
        while (running) {
            inputLock.lock()
            while (running && (commands.empty || !ready)) {
                readyCondition.await()
            }
            var command: AbstractCommand? = null
            if (running) {
                command = commands.removeLast()
                ready = false
            }
            inputLock.unlock()

            if (command != null) {
                debugger.execute(command!!)
            }
        }
    }

    public fun addCommand(command: AbstractCommand) {
        inputLock.lock()
        commands.addFirst(command)
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