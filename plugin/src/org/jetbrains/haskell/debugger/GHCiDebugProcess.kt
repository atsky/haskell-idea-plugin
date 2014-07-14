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
import com.intellij.execution.process.ProcessListener
import java.util.concurrent.atomic.AtomicBoolean
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import java.util.concurrent.ConcurrentMap
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by vlad on 7/10/14.
 */

public class GHCiDebugProcess(session: XDebugSession,
                              val executionConsole: ExecutionConsole,
                              val myProcessHandler: ProcessHandler) : XDebugProcess(session), ProcessListener {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider
    private val debugger: GHCiDebugger

    public val readyForInput: AtomicBoolean = AtomicBoolean(false);

    {
        debuggerEditorsProvider = HaskellDebuggerEditorsProvider()
        debugger = GHCiDebugger(this)

        myProcessHandler.addProcessListener(this)
    }

    private val _breakpointHandlers: ArrayList<XBreakpointHandler<*>>
    {
        _breakpointHandlers = ArrayList<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>>()
        _breakpointHandlers.add(HaskellLineBreakpointHandler(javaClass<HaskellLineBreakpointType>(), this))
        tryAddBreakpointHandlersFromExtensions()
    }
    private val registeredBreakpoints: MutableMap<Int, XLineBreakpoint<*>> = ConcurrentHashMap()

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

    public fun addBreakpoint(position: Int, breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
        registeredBreakpoints.put(position, breakpoint)
        // isConnected check is omitted
//        debugger.setBreakpoint(breakpoint.getType().getId(), position.fileName, position.line, breakpoint.getCondition(), breakpoint.getLogExpression())
        debugger.setBreakpoint(position)
    }

    public fun removeBreakpoint(position: Int) {
        val breakpoint = registeredBreakpoints.get(position)
        if (breakpoint != null) {
            registeredBreakpoints.remove(position)
            // isConnected check is omitted
//            debugger.removeBreakpoint(breakpoint.getType().getId(), position.fileName, position.line)
            debugger.removeBreakpoint(position)
        }
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


    public fun printToConsole(text: String) {
        (executionConsole as ConsoleView).print(text, ConsoleViewContentType.NORMAL_OUTPUT)
    }


    // ProcessListener

    override fun startNotified(event: ProcessEvent?) {
    }

    override fun processTerminated(event: ProcessEvent?) {
    }

    override fun processWillTerminate(event: ProcessEvent?, willBeDestroyed: Boolean) {
    }

    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        print(event?.getText())
        if (isReadyForInput(event?.getText())) {
            readyForInput.set(true)
        }
    }

    private fun isReadyForInput(line: String?): Boolean = "*Main>".equals(line?.trim())    //temporary

}