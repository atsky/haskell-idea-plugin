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
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import java.util.ArrayList
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointType
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointHandler
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.protocol.SequenceOfBacksCommand
import org.jetbrains.haskell.debugger.parser.HsCommonStackFrameInfo
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by vlad on 7/10/14.
 */

public class HaskellDebugProcess(session: XDebugSession,
                                 val executionConsole: ExecutionConsole,
                                 val myProcessHandler: ProcessHandler) : XDebugProcess(session), ProcessListener {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider

    public val debugger: ProcessDebugger

    ;{
        debuggerEditorsProvider = HaskellDebuggerEditorsProvider()
        debugger = GHCiDebugger(this)

        myProcessHandler.addProcessListener(this)

    }

    private val _breakpointHandlers: Array<XBreakpointHandler<*>> = array(
            HaskellLineBreakpointHandler(getSession()!!.getProject(), javaClass<HaskellLineBreakpointType>(), this)
    )

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>> {
        return _breakpointHandlers
    }

    private class BreakpointPosition(val module: String, val line: Int) {
        override fun equals(other: Any?): Boolean {
            if (other == null || other !is BreakpointPosition) {
                return false
            }
            return module.equals(other.module) && line.equals(other.line)
        }
        override fun hashCode(): Int {
            return module.hashCode() * 31 + line
        }
    }
    private class BreakpointEntry(var breakpointNumber: Int?, val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>)
    private val registeredBreakpoints: MutableMap<BreakpointPosition, BreakpointEntry> = hashMapOf()

    public fun setBreakpointNumberAtLine(breakpointNumber: Int, module: String, line: Int) {
        val entry = registeredBreakpoints.get(BreakpointPosition(module, line))
        if (entry != null) {
            entry.breakpointNumber = breakpointNumber
        }
    }

    public fun getBreakpointAtPosition(module: String, line: Int): XLineBreakpoint<XBreakpointProperties<*>>? {
        return registeredBreakpoints.get(BreakpointPosition(module, line))?.breakpoint
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
        debugger.stepOver()
    }

    override fun startStepInto() {
        debugger.stepInto()
    }

    override fun startStepOut() {
        throw UnsupportedOperationException()
    }

    override fun stop() {
        debugger.close()
    }

    override fun resume() {
        debugger.resume()
    }

    override fun runToPosition(position: XSourcePosition) {
        debugger.runToPosition(
                HaskellUtils.getModuleName(getSession()!!.getProject(), position.getFile()),
                HaskellUtils.zeroBasedToHaskellLineNumber(position.getLine()))
    }

    public fun addBreakpoint(module: String, line: Int, breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
        registeredBreakpoints.put(BreakpointPosition(module, line), BreakpointEntry(null, breakpoint))
        //        println(module)
        debugger.setBreakpoint(module, line)
    }

    public fun removeBreakpoint(module: String, line: Int) {
        val breakpointNumber: Int? = registeredBreakpoints.get(BreakpointPosition(module, line))?.breakpointNumber
        if (breakpointNumber != null) {
            registeredBreakpoints.remove(BreakpointPosition(module, line))
            debugger.removeBreakpoint(breakpointNumber)
        }
    }

    public fun fillFramesFromHistory(allHistFramesArray: ArrayList<HsCommonStackFrameInfo>,
                                     syncObject: Lock,
                                     frameIsFilled: Condition,
                                     frameHistoryIndex: Int) {
        debugger.backsSequence(SequenceOfBacksCommand(allHistFramesArray, syncObject, frameIsFilled, frameHistoryIndex,
                debugProcess = this))
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
        val text = event?.getText()
        if (text != null) {
            print(text)
            debugger.onTextAvailable(text, outputType)
        }
    }
}