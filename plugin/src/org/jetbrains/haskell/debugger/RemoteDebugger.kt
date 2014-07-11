package org.jetbrains.haskell.debugger

import java.net.ServerSocket
import org.jetbrains.haskell.debugger.commands.AbstractCommand
import java.net.Socket
import org.jetbrains.haskell.debugger.commands.RunCommand

/**
 * Created by vlad on 7/11/14.
 */

public class RemoteDebugger(val debugProcess: GHCiDebugProcess,
                            val serverSocket: ServerSocket,
                            val timeout: Int) : ProcessDebugger {

    private var socket: Socket? = null
    private var connected = false
    private val lockObject = Any()

    override fun waitForConnect() {
        synchronized(lockObject) {
            serverSocket.setSoTimeout(timeout)
            socket = serverSocket.accept()
            connected = true
        }
    }

    override fun run() {
        execute(RunCommand())
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
            val os = socket!!.getOutputStream()!!
            os.write(bytes)
            os.flush()
        }
    }

}