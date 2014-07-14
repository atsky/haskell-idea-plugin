package org.jetbrains.haskell.debugger

import java.net.ServerSocket
import org.jetbrains.haskell.debugger.commands.AbstractCommand
import java.net.Socket
import org.jetbrains.haskell.debugger.commands.RunCommand

/**
 * Created by vlad on 7/11/14.
 */

public class GHCiDebugger(val debugProcess: GHCiDebugProcess) : ProcessDebugger {


    private val lockObject = Any()

    override fun run() {
        println("RUN executed")
//        execute(RunCommand())
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
            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(bytes)
            os.flush()
        }
    }

}