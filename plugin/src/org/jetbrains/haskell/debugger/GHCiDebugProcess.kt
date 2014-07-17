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
import org.jetbrains.haskell.debugger.protocol.SetBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import com.intellij.xdebugger.frame.XSuspendContext
import org.jetbrains.haskell.debugger.protocol.StepIntoCommand
import org.jetbrains.haskell.debugger.protocol.StepOverCommand
import org.jetbrains.haskell.debugger.protocol.ResumeCommand
import java.io.File
import java.util.regex.Pattern
import java.util.ArrayList
import com.intellij.execution.process.ProcessOutputTypes
import org.jetbrains.haskell.debugger.protocol.HistoryCommand
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import java.util.LinkedList
import java.util.Deque

/**
 * Created by vlad on 7/10/14.
 */

public class GHCiDebugProcess(session: XDebugSession,
                              val executionConsole: ExecutionConsole,
                              val myProcessHandler: ProcessHandler) : XDebugProcess(session), ProcessListener {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider
    public val debugger: GHCiDebugger
    private val inputReadinessListener: InputReadinessListener

    public val readyForInput: AtomicBoolean = AtomicBoolean(false)
    public val allOutputAccepted: AtomicBoolean = AtomicBoolean(false)
    private val collectedOutput: Deque<String?> = LinkedList()

    public val debugFinished: Boolean = false;

    {
        debuggerEditorsProvider = HaskellDebuggerEditorsProvider()
        debugger = GHCiDebugger(this)

        myProcessHandler.addProcessListener(this)

        inputReadinessListener = InputReadinessListener(this)
        inputReadinessListener.start()
    }

    private val _breakpointHandlers: Array<XBreakpointHandler<*>> = array(
            HaskellLineBreakpointHandler(javaClass<HaskellLineBreakpointType>(), this)
    )

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>> {
        return _breakpointHandlers
    }

    private class BreakpointEntry(var breakpointNumber: Int?, val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>)
    private val registeredBreakpoints: MutableMap<Int, BreakpointEntry> = hashMapOf()

    public fun setBreakpointNumberAtLine(breakpointNumber: Int, line: Int) {
        val entry = registeredBreakpoints.get(line)
        if (entry != null) {
            entry.breakpointNumber = breakpointNumber
        }
    }

    public fun getBreakpointAtLine(line: Int): XLineBreakpoint<XBreakpointProperties<*>>? {
        return registeredBreakpoints.get(line)?.breakpoint
    }

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
        debugger.stepOver()
    }

    override fun startStepInto() {
        debugger.stepInto()
    }

    override fun startStepOut() {
        throw UnsupportedOperationException()
    }

    override fun stop() {
        debugger.close();
    }

    override fun resume() {
        debugger.resume()
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
        debugger.prepareGHCi()
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
        if (outputType == ProcessOutputTypes.STDOUT) {
            val text = event?.getText()
            print(text)
            collectedOutput.addFirst(text)
            if(allOutputAccepted.get()) {
                handleGHCiOutput()
                allOutputAccepted.set(false)
                readyForInput.set(true)
            }
        } else if (outputType == ProcessOutputTypes.STDERR) {
            print(event?.getText())
        }
        if (!inputReadinessListener.connected && isReadyForInput(event?.getText())) {
            readyForInput.set(true)
        }
    }

    private fun isReadyForInput(line: String?): Boolean =
            line?.endsWith(PROMPT_LINE) ?: false

    // methods to handle GHCi output
    private fun handleGHCiOutput() {
        if (!collectedOutput.empty) {
            debugger.lastCommand?.handleOutput(collectedOutput, this)
            collectedOutput.clear()
        }
    }

    class object {

//        private val BREAKPOINT_ACTIVATED_PATTERN = "Breakpoint (\\d)+ activated at *:(\\d)+:(\\d)+-(\\d)+"

        public val PROMPT_LINE: String = "debug> "

        // todo: change
        public val INPUT_READINESS_PORT: Int = 12345
    }
}