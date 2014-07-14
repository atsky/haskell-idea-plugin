package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.XSourcePosition
import com.intellij.execution.process.ProcessHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointHandler

/**
 * Created by vlad on 7/10/14.
 */

public class GHCiDebugProcess(session: XDebugSession,
                              val executionConsole: ExecutionConsole,
                              val myProcessHandler: ProcessHandler) : XDebugProcess(session) {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider
    private val debugger: ProcessDebugger

    {
        debuggerEditorsProvider = HaskellDebuggerEditorsProvider()
        debugger = GHCiDebugger(this)
    }

    private var _breakpointHandlers: Array<XBreakpointHandler<*>>
    {
        _breakpointHandlers = array(HaskellLineBreakpointHandler(javaClass<HaskellLineBreakpointType>(), this))
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return debuggerEditorsProvider
    }

    override fun doGetProcessHandler(): ProcessHandler? {
        return myProcessHandler
    }

    override fun createConsole(): ExecutionConsole {
        return executionConsole
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
    }

    override fun resume() {
        throw UnsupportedOperationException()
    }

    override fun runToPosition(position: XSourcePosition) {
        throw UnsupportedOperationException()
    }

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>> {
        return _breakpointHandlers
    }
}