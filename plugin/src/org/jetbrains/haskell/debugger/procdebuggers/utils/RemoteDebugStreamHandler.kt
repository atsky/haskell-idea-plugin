package org.jetbrains.haskell.debugger.procdebuggers.utils

import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessHandler
import java.net.ServerSocket
import java.io.BufferedReader
import java.io.InputStreamReader
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutputTypes

public class RemoteDebugStreamHandler: Runnable {

    private var running = true
    private val serverSocket = ServerSocket(0)
    public var listener: ProcessListener? = null
    public var processHandler: ProcessHandler? = null

    override fun run() {
        val socket = serverSocket.accept()
        val br = BufferedReader(InputStreamReader(socket.getInputStream()!!))
        var line: String?
        while (running) {
            line = br.readLine()
            if (line == null) {
                stop()
            } else {
                listener?.onTextAvailable(ProcessEvent(processHandler, line + "\n"), ProcessOutputTypes.STDOUT)
            }

        }
    }

    public fun start() {
        Thread(this).start()
    }

    public fun stop() {
        running = false
        serverSocket.close()
    }

    public fun getPort(): Int {
        return serverSocket.getLocalPort()
    }
}