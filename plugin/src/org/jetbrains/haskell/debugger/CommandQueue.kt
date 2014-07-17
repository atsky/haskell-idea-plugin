package org.jetbrains.haskell.debugger

import java.util.Queue
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import com.sun.jmx.remote.internal.ArrayQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.LinkedList

/**
 * Created by vlad on 7/15/14.
 */

public class CommandQueue(val debugger: GHCiDebugger, val flag: AtomicBoolean) : Runnable {

    private val commands = LinkedList<AbstractCommand>()
    private var running = true

    override fun run() {
        while (running) {
            if (commands.empty || !flag.getAndSet(false)) {
                Thread.sleep(100);
            } else {
                val command = removeCommand()
                debugger.execute(command)
            }
        }
    }

    public synchronized fun addCommand(command: AbstractCommand) {
        commands.addFirst(command)
    }

    private synchronized fun removeCommand(): AbstractCommand {
        return commands.removeLast()
    }

    public fun start() {
        Thread(this).start()
    }

    public fun stop() {
        running = false
    }

}