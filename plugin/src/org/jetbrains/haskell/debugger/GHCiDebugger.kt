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

    override fun trace() {
        execute(TraceCommand())
    }

    override fun addBreakPoint(file: String, line: String) {
        throw UnsupportedOperationException()
    }

    override fun removeBreakPoint(file: String, line: String) {
        throw UnsupportedOperationException()
    }

    override fun execute(command: AbstractCommand) {
        val bytes = command.getBytes()

        // Wait for the line "*Main> " to appear (if this line is an output of program, may be misunderstood),
        // needed for correct displaying of input/output of the process in console.
        // Another variant could be: execute command immediately, but display in console later.
        while (debugProcess.readyForInput.compareAndSet(false, false)) {
            Thread.sleep(100)
        }

        synchronized(lockObject) {
            System.out.write(bytes)
            System.out.flush()

            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(bytes)
            os.flush()
        }
    }

}