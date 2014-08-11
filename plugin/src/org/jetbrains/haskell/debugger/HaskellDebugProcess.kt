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
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointType
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointHandler
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import org.jetbrains.haskell.debugger.highlighting.HsDebugSessionListener
import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.concurrent.locks.ReentrantLock
import org.jetbrains.haskell.debugger.protocol.ForceCommand
import org.jetbrains.haskell.debugger.config.HaskellDebugSettings
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import com.intellij.xdebugger.ui.XDebugTabLayouter
import com.intellij.openapi.actionSystem.DefaultActionGroup
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.protocol.SyncCommand
import org.jetbrains.haskell.debugger.utils.SyncObject
import org.jetbrains.haskell.debugger.breakpoints.HaskellExceptionBreakpointHandler
import org.jetbrains.haskell.debugger.breakpoints.HaskellExceptionBreakpointProperties
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import org.jetbrains.haskell.debugger.protocol.BreakpointListCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointByIndexCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointCommand
import org.jetbrains.haskell.debugger.parser.BreakInfo
import org.jetbrains.haskell.debugger.parser.HsHistoryFrameInfo

/**
 * Created by vlad on 7/10/14.
 */

public class HaskellDebugProcess(session: XDebugSession,
                                 val executionConsole: ExecutionConsole,
                                 val myProcessHandler: HaskellDebugProcessHandler) : XDebugProcess(session), ProcessListener {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider
    public val historyManager: HistoryManager = HistoryManager(this)

    public val debugger: ProcessDebugger;

    {
        debuggerEditorsProvider = HaskellDebuggerEditorsProvider()
        debugger =
                if (HaskellDebugSettings.getInstance().getState().debuggerType == HaskellDebugSettings.DebuggerType.GHCI)
                    GHCiDebugger(this)
                else
                    RemoteDebugger(this)

        myProcessHandler.setDebugProcessListener(this)
    }

    private val _breakpointHandlers: Array<XBreakpointHandler<*>> = array(
            HaskellLineBreakpointHandler(getSession()!!.getProject(), javaClass<HaskellLineBreakpointType>(), this),
            HaskellExceptionBreakpointHandler(this)
    )

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>> {
        return _breakpointHandlers
    }

    public var exceptionBreakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>? = null
        private set

    public fun addExceptionBreakpoint(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        exceptionBreakpoint = breakpoint
        debugger.setExceptionBreakpoint(breakpoint.getProperties()!!.getState().exceptionType ==
                HaskellExceptionBreakpointProperties.ExceptionType.ERROR)
    }

    public fun removeExceptionBreakpoint(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        exceptionBreakpoint = null
        debugger.removeExceptionBreakpoint()
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
        historyManager.clean()
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
        debugger.setBreakpoint(module, line)
    }

    public fun addBreakpointByIndex(module: String, index: Int, breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
        if(HaskellDebugSettings.getInstance().getState().debuggerType == HaskellDebugSettings.DebuggerType.REMOTE) {
            registeredBreakpoints.put(BreakpointPosition(module, HaskellUtils.zeroBasedToHaskellLineNumber(breakpoint.getLine())),
                                      BreakpointEntry(index, breakpoint))
            val command = SetBreakpointByIndexCommand(module, index, SetBreakpointCommand.StandardSetBreakpointCallback(module, this))
            debugger.enqueueCommand(command)
        } else {
            throw RuntimeException("Only remote debugger supports breakpoint setting by index")
        }
    }

    public fun removeBreakpoint(module: String, line: Int) {
        val breakpointNumber: Int? = registeredBreakpoints.get(BreakpointPosition(module, line))?.breakpointNumber
        if (breakpointNumber != null) {
            registeredBreakpoints.remove(BreakpointPosition(module, line))
            debugger.removeBreakpoint(module, breakpointNumber)
        }
    }

    public fun forceSetValue(localBinding: LocalBinding) {
        if (localBinding.name != null) {
            val syncObject: Lock = ReentrantLock()
            val bindingValueIsSet: Condition = syncObject.newCondition()
            val syncLocalBinding: LocalBinding = LocalBinding(localBinding.name, "", null)
            syncObject.lock()
            try {
                historyManager.withRealFrameUpdate {
                    (_) ->
                    debugger.force(ForceCommand(localBinding.name!!,
                        ForceCommand.StandardForceCallback(syncLocalBinding, syncObject, bindingValueIsSet, this)))
                }
                while (syncLocalBinding.value == null) {
                    bindingValueIsSet.await()
                }
                if (syncLocalBinding.value?.isNotEmpty() ?: false) {
                    localBinding.value = syncLocalBinding.value
                }
            } finally {
                syncObject.unlock()
            }
        }
    }

    public fun breakListForLine(moduleName: String, lineNumber: Int): ArrayList<BreakInfo> {
        if(HaskellDebugSettings.getInstance().getState().debuggerType == HaskellDebugSettings.DebuggerType.REMOTE) {
            val syncObject = SyncObject()
            val resultArray: ArrayList<BreakInfo> = ArrayList()
            val callback = BreakpointListCommand.DefaultCallback(syncObject, resultArray)
            val command = BreakpointListCommand(moduleName, lineNumber, callback)
            syncCommand(command, syncObject)
            return resultArray
        }
        return ArrayList()
    }

    /**
     * Used to make synchronous requests to debugger.
     *
     * @see org.jetbrains.haskell.debugger.utils.SyncObject
     */
    private fun syncCommand(command: SyncCommand<*>, syncObject: SyncObject) {
        syncObject.lock()
        try {
            debugger.enqueueCommand(command)
            while (!syncObject.signaled()) {
                syncObject.await()
            }
        } finally {
            syncObject.unlock()
        }
    }

    override fun sessionInitialized() {
        super<XDebugProcess>.sessionInitialized()
        val currentSession = getSession()
        currentSession?.addSessionListener(HsDebugSessionListener(currentSession as XDebugSession))
        debugger.prepareDebugger()
        debugger.trace()
    }


    public fun printToConsole(text: String, contentType: ConsoleViewContentType = ConsoleViewContentType.NORMAL_OUTPUT) {
        when (contentType) {
            ConsoleViewContentType.ERROR_OUTPUT -> {
                System.err.print(text)
                System.err.flush()
            }
            else -> {
                System.out.print(text)
                System.out.flush()
            }
        }
        (executionConsole as ConsoleView).print(text, contentType)
    }

    override fun createTabLayouter(): XDebugTabLayouter {
        return historyManager
    }

    override fun registerAdditionalActions(leftToolbar: DefaultActionGroup, topToolbar: DefaultActionGroup) {
        historyManager.registerActions(leftToolbar, topToolbar)
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
            if (debugger is RemoteDebugger) {
                (executionConsole as ConsoleView).print(text, ConsoleViewContentType.SYSTEM_OUTPUT)
            }
            debugger.onTextAvailable(text, outputType)
        }
    }
}