package org.jetbrains.haskell.debugger

import java.net.ServerSocket

/**
 * Created by vlad on 7/16/14.
 */

public class InputReadinessListener(val debugProcess: GHCiDebugProcess) : Runnable {

    private var running = true
    public var connected: Boolean = false
        private set

    private val serverSocket: ServerSocket = ServerSocket(GHCiDebugProcess.INPUT_READINESS_PORT)

    override fun run() {

        // todo: need to be careful with stopping the process
        try {
            while (running) {
                /*
                 * Need to always reopen connection (not because of hClose command in ghci)
                 */
                val socket = serverSocket.accept()
                connected = true
                val inputStream = socket.getInputStream()!!
                val b = inputStream.read()
                if (b == 0) {
                    debugProcess.readyForInput.set(true)
                } else {
                    debugProcess.getSession()?.stop()
                    running = false
                }
                connected = false
                socket.close()
            }
            serverSocket.close()
        } catch (e: Exception) {
        }
    }

    public fun start() {
        Thread(this).start()
    }

    public fun stop() {
        running = false
        connected = false
        serverSocket.close()
    }

}
