package org.jetbrains.haskell.debugger

import java.util.Queue
import org.jetbrains.haskell.debugger.commands.AbstractCommand
import com.sun.jmx.remote.internal.ArrayQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by vlad on 7/15/14.
 */

public class CommandQueue(val debugger: GHCiDebugger, val flag: AtomicBoolean) : Runnable {

    private val commands = ArrayQueue<AbstractCommand>(10)
    private var running = true

    override fun run() {
        while (running) {
            if (commands.empty || flag.compareAndSet(false, false)) {
                Thread.sleep(100);
            } else {
                val command = commands.remove(0)
                debugger.execute(command)
            }
        }
    }

    public synchronized fun addCommand(command: AbstractCommand) {
        commands.add(command)
    }

    public fun start() {
        Thread(this).start()
    }

    public fun stop() {
        running = false
    }

}