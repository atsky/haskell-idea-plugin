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
import com.intellij.execution.process.ProcessListener
import java.util.concurrent.atomic.AtomicBoolean
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.frame.XSuspendContext

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

    private val _breakpointHandlers: Array<XBreakpointHandler<*>> = array(
            HaskellLineBreakpointHandler(javaClass<HaskellLineBreakpointType>(), this)
    )

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>> {
        return _breakpointHandlers
    }

    private class BreakpointEntry(var breakpointNumber: Int?, val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>)
    private val registeredBreakpoints: MutableMap<Int, BreakpointEntry> = hashMapOf()

    //    private fun tryAddBreakpointHandlersFromExtensions() {
    //        val extPointName: ExtensionPointName<HaskellBreakpointHandlerFactory>? = HaskellBreakpointHandlerFactory.EXTENSION_POINT_NAME
    //        if(extPointName != null) {
    //            for (factory in Extensions.getExtensions(extPointName)) {
    //                _breakpointHandlers.add(factory.createBreakpointHandler(this))
    //            }
    //        }
    //    }

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
        debugger.close();
    }

    override fun resume() {
        throw UnsupportedOperationException()
    }

    override fun runToPosition(position: XSourcePosition) {
        throw UnsupportedOperationException()
    }

    public fun addBreakpoint(position: Int, breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
        registeredBreakpoints.put(position, BreakpointEntry(null, breakpoint))
        debugger.setBreakpoint(position)
    }

    public fun removeBreakpoint(position: Int) {
        val breakpointNumber: Int? = registeredBreakpoints.get(position)?.breakpointNumber
        if (breakpointNumber != null) {
            registeredBreakpoints.remove(position)
            debugger.removeBreakpoint(breakpointNumber)
        }
    }

    override fun sessionInitialized() {
        super<XDebugProcess>.sessionInitialized()
        debugger.trace()
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
        handleGHCiOutput(event?.getText())

        if (isReadyForInput(event?.getText())) {
            readyForInput.set(true)
        }
    }

    private fun isReadyForInput(line: String?): Boolean = "*Main>".equals(line?.trim())    //temporary

    // methods to handle GHCi output
    private fun handleGHCiOutput(output: String?) {
        if (output != null) {
            when (debugger.lastCommand?.commandName) {
                "SetBreakpoint" -> tryHandleSetBreakpointCommandResult(output)
                "Trace" -> tryHandleStoppedAtBreakpoint(output)
            }

        }
    }

    private fun tryHandleSetBreakpointCommandResult(output: String) {
        //temporary and not optimal, later parser should do this work (added just for testing)
        val parts = output.split(' ')

        if (parts.size > 4 && parts[0] == "Breakpoint" && parts[2] == "activated" && parts[3] == "at") {
            val breakpointNumber = parts[1].toInt()
            val lastWord = parts[parts.size - 1]
            val lineNumberBegSubstr = lastWord.substring(lastWord.indexOf(':') + 1)
            val lineNumber = lineNumberBegSubstr.substring(0, lineNumberBegSubstr.indexOf(':')).toInt()
            val entry = registeredBreakpoints.get(lineNumber)
            if (entry != null) {
                entry.breakpointNumber = breakpointNumber
            }
        }
    }

    private fun tryHandleStoppedAtBreakpoint(output: String) {
        if (!output.startsWith("Stopped at")) {
            return
        }
        try {
            val secondColon: Int = output.lastIndexOf(':')
            val firstColon: Int = output.lastIndexOf(':', secondColon - 1)
            val lineNumber: Int = Integer.parseInt(output.substring(firstColon + 1, secondColon))
            val breakpoint = registeredBreakpoints.get(lineNumber)!!.breakpoint
            val context = object : XSuspendContext() {}
            getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
        } catch (e: Exception) {
        }
    }
}