package org.jetbrains.haskell.debugger

import java.net.ServerSocket
import org.jetbrains.haskell.debugger.commands.AbstractCommand
import java.net.Socket
import org.jetbrains.haskell.debugger.commands.TraceCommand
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by vlad on 7/11/14.
 */

public class GHCiDebugger(val debugProcess: GHCiDebugProcess) : ProcessDebugger {

    private val lockObject = Any()
    private val queue: CommandQueue

    {
        queue = CommandQueue(this, debugProcess.readyForInput)
        queue.start()
    }

    override fun trace() {
        queue.addCommand(TraceCommand())
    }

    override fun addBreakPoint(file: String, line: String) {
        throw UnsupportedOperationException()
    }

    override fun removeBreakPoint(file: String, line: String) {
        throw UnsupportedOperationException()
    }

    override fun execute(command: AbstractCommand) {
        val bytes = command.getBytes()

        synchronized(lockObject) {
            debugProcess.printToConsole(String(bytes))

            System.out.write(bytes)
            System.out.flush()

            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(bytes)
            os.flush()
        }
    }

}