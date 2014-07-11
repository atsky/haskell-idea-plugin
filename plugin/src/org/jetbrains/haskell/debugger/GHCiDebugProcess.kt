package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import java.net.ServerSocket
import com.intellij.execution.ui.ExecutionConsole
import sun.java2d.loops.ProcessPath.ProcessHandler
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.XSourcePosition

/**
 * Created by vlad on 7/10/14.
 */

public class GHCiDebugProcess(session: XDebugSession,
                              ghciProcess: Process,
                              executionConsole: ExecutionConsole,
                              processHandler: ProcessHandler) : XDebugProcess(session) {

    class object {
        private val CONNECTION_TIMEOUT = 60000
    }

    private val debugger: ProcessDebugger

    {
        debugger = GHCiDebugger(this, ghciProcess)
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        throw UnsupportedOperationException()
    }

    override fun startStepOver() {
        throw UnsupportedOperationException()
    }

    override fun startStepInto() {
        throw UnsupportedOperationException()
    }

    override fun startStepOut() {
        throw UnsupportedOperationException()
    }

    override fun stop() {
        throw UnsupportedOperationException()
    }

    override fun resume() {
        throw UnsupportedOperationException()
    }

    override fun runToPosition(position: XSourcePosition) {
        throw UnsupportedOperationException()
    }


}