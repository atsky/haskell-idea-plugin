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
import com.intellij.openapi.extensions.Extensions
import java.util.ArrayList
import com.intellij.openapi.extensions.ExtensionPointName

/**
 * Created by vlad on 7/10/14.
 */

public class GHCiDebugProcess(session: XDebugSession,
                              val executionConsole: ExecutionConsole,
                              val myProcessHandler: ProcessHandler,
                              listener: HaskellDebugProcessListener) : XDebugProcess(session) {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider
    private val debugger: GHCiDebugger

    {
        debuggerEditorsProvider = HaskellDebuggerEditorsProvider()
        debugger = GHCiDebugger(this, listener)
    }

    private var _breakpointHandlers: ArrayList<XBreakpointHandler<*>>
    {
        _breakpointHandlers = ArrayList<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>>()
        _breakpointHandlers.add(HaskellLineBreakpointHandler(javaClass<HaskellLineBreakpointType>(), this))
        tryAddBreakpointHandlersFromExtensions()
    }

    private fun tryAddBreakpointHandlersFromExtensions() {
        val extPointName: ExtensionPointName<HaskellBreakpointHandlerFactory>? = HaskellBreakpointHandlerFactory.EXTENSION_POINT_NAME
        if(extPointName != null) {
            for (factory in Extensions.getExtensions(extPointName)) {
                _breakpointHandlers.add(factory.createBreakpointHandler(this))
            }
        }
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
        // bad decision but for now I don't know how to convert ArrayList to Array better
        return _breakpointHandlers.toArray(Array<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>>(
                _breakpointHandlers.size, {i -> HaskellLineBreakpointHandler(javaClass<HaskellLineBreakpointType>(), this)}))
    }

    override fun sessionInitialized() {
        super<XDebugProcess>.sessionInitialized()
        // will be changed when I find the correct place, where to invoke method trace()
        object: Thread() {
            override fun run() {
                debugger.trace()
            }
        }.start()
    }


}