package org.jetbrains.haskell.debugger.procdebuggers.utils

import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import org.jetbrains.haskell.debugger.GHCiDebugProcessStateUpdater

public class InputReadinessChecker(val debugStateUpdater: GHCiDebugProcessStateUpdater)
: Runnable {

    class object {
        private val OUTPUT_ACCEPTED_BYTE: Int = 0
    }

    private var running = true
    public var connected: Boolean = false
        private set

    private val serverSocket: ServerSocket = ServerSocket(0)
    public val INPUT_READINESS_PORT: Int = serverSocket.getLocalPort()

    override fun run() {

        var socket: Socket? = null
        try {
            while (running) {
                /*
                 * Need to always reopen connection (not because of hClose command in ghci)
                 */
                try {
                    socket = serverSocket.accept()
                    connected = true
                } catch (e: SocketException) {
                    println(e.getMessage())
                    running = false
                }
                if (connected) {
                    socket!!.getInputStream()!!.read()
                    debugStateUpdater.processStopped.set(true)
                    debugStateUpdater.checkCollected()
                    connected = false
                    socket!!.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            running = false
            connected = false
            socket?.close()
            if (!serverSocket.isClosed()) {
                serverSocket.close()
            }
        }
    }

    public fun start() {
        Thread(this).start()
    }

    public fun stop() {
        running = false
        if (!serverSocket.isClosed()) {
            serverSocket.close()
        }
    }

}