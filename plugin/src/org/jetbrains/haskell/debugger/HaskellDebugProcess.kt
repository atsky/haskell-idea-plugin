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
import com.intellij.execution.ui.ConsoleView
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
import com.intellij.xdebugger.ui.XDebugTabLayouter
import com.intellij.openapi.actionSystem.DefaultActionGroup
import org.jetbrains.haskell.debugger.protocol.SyncCommand
import org.jetbrains.haskell.debugger.utils.SyncObject
import org.jetbrains.haskell.debugger.breakpoints.HaskellExceptionBreakpointHandler
import org.jetbrains.haskell.debugger.breakpoints.HaskellExceptionBreakpointProperties
import java.util.ArrayList
import org.jetbrains.haskell.debugger.protocol.BreakpointListCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointByIndexCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointCommand
import org.jetbrains.haskell.debugger.parser.BreakInfo
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.xdebugger.impl.actions.StepOutAction
import com.intellij.xdebugger.impl.actions.ForceStepIntoAction
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger
import org.jetbrains.haskell.debugger.procdebuggers.GHCiDebugger
import org.jetbrains.haskell.debugger.procdebuggers.RemoteDebugger
import org.jetbrains.haskell.debugger.history.HistoryManager
import org.jetbrains.haskell.debugger.prochandlers.HaskellDebugProcessHandler
import com.intellij.execution.ui.RunnerLayoutUi
import org.jetbrains.haskell.debugger.repl.DebugConsoleFactory
import java.util.Deque
import com.intellij.xdebugger.frame.XSuspendContext
import java.util.ArrayDeque
import org.jetbrains.haskell.debugger.procdebuggers.utils.DefaultRespondent
import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent

/**
 * Main class for managing debug process and sending commands to real debug process through it's ProcessDebugger member.
 *
 * Attention! When sending commands to the underlying ProcessDebugger they are enqueued. But some commands may require
 * a lot of time to finish and, for example, if you call asynchronous command that needs much time to finish and
 * after that call synchronous command that freezes UI thread, you will get all the UI frozen until the first
 * command is finished. To check no command is in progress use
 * {@link org.jetbrains.haskell.debugger.HaskellDebugProcess#isReadyForNextCommand}
 *
 * @see org.jetbrains.haskell.debugger.HaskellDebugProcess#isReadyForNextCommand
 */

public class HaskellDebugProcess(session: XDebugSession,
                                 val executionConsole: ExecutionConsole,
                                 val _processHandler: HaskellDebugProcessHandler,
                                 val stopAfterTrace: Boolean)
: XDebugProcess(session) {

    public val historyManager: HistoryManager = HistoryManager(this)
    public var exceptionBreakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>? = null
        private set
    public val debugger: ProcessDebugger

    private val debugRespondent: DebugRespondent = DefaultRespondent(this)
    private val contexts: Deque<XSuspendContext> = ArrayDeque()
    private val debugProcessStateUpdater: DebugProcessStateUpdater
    private val _editorsProvider: XDebuggerEditorsProvider = HaskellDebuggerEditorsProvider()
    private val _breakpointHandlers: Array<XBreakpointHandler<*>> = array(
            HaskellLineBreakpointHandler(getSession()!!.getProject(), javaClass<HaskellLineBreakpointType>(), this),
            HaskellExceptionBreakpointHandler(this)
    )
    private val registeredBreakpoints: MutableMap<BreakpointPosition, BreakpointEntry> = hashMapOf()

    private val BREAK_BY_INDEX_ERROR_MSG = "Only remote debugger supports breakpoint setting by index";

    {
        val debuggerIsGHCi = HaskellDebugSettings.getInstance().getState().debuggerType ==
                HaskellDebugSettings.DebuggerType.GHCI
        if (debuggerIsGHCi) {
            debugProcessStateUpdater = GHCiDebugProcessStateUpdater(this)
            debugger = GHCiDebugger(debugRespondent, _processHandler,
                    executionConsole as ConsoleView,
                    (debugProcessStateUpdater as GHCiDebugProcessStateUpdater).INPUT_READINESS_PORT)
        } else {
            debugProcessStateUpdater = RemoteDebugProcessStateUpdater(this)
            debugger = RemoteDebugger(debugRespondent, _processHandler)
        }
        _processHandler.setDebugProcessListener(debugProcessStateUpdater)
    }

    // XDebugProcess methods overriding

    override fun getEditorsProvider(): XDebuggerEditorsProvider = _editorsProvider

    override fun getBreakpointHandlers()
            : Array<XBreakpointHandler<out XBreakpoint<out XBreakpointProperties<out Any?>?>?>> = _breakpointHandlers

    override fun doGetProcessHandler(): ProcessHandler? = _processHandler

    override fun createConsole(): ExecutionConsole = executionConsole

    override fun startStepOver() = debugger.stepOver()

    override fun startStepInto() = debugger.stepInto()

    override fun startStepOut() {
        val msg = "'Step out' not implemented"
        Notifications.Bus.notify(Notification("", "Debug execution error", msg, NotificationType.WARNING))
        getSession()!!.positionReached(getSession()!!.getSuspendContext()!!)
    }

    override fun stop() {
        historyManager.clean()
        debugger.close()
        debugProcessStateUpdater.close()
    }

    override fun resume() = debugger.resume()

    override fun runToPosition(position: XSourcePosition) =
            debugger.runToPosition(
                    HaskellUtils.getModuleName(getSession()!!.getProject(), position.getFile()),
                    HaskellUtils.zeroBasedToHaskellLineNumber(position.getLine()))

    override fun sessionInitialized() {
        super<XDebugProcess>.sessionInitialized()
        val currentSession = getSession()
        currentSession?.addSessionListener(HsDebugSessionListener(currentSession as XDebugSession))
        debugger.prepareDebugger()
        if (stopAfterTrace) {
            debugger.trace(null)
        }
    }

    override fun createTabLayouter(): XDebugTabLayouter = object : XDebugTabLayouter() {
        override fun registerAdditionalContent(ui: RunnerLayoutUi) {
            historyManager.registerContent(ui)
            val repl = DebugConsoleFactory.createDebugConsole(getSession()!!.getProject(), this@HaskellDebugProcess, _processHandler)
            val consoleContext = ui.createContent("REPL", repl.getComponent()!!, "REPL Console", null, repl)
            consoleContext.setCloseable(false)
            ui.addContent(consoleContext)
        }
    }

    override fun registerAdditionalActions(leftToolbar: DefaultActionGroup, topToolbar: DefaultActionGroup) {
        //temporary code for removal of unused actions from debug panel
        var stepOut: StepOutAction? = null
        var forceStepInto: ForceStepIntoAction? = null
        for (action in topToolbar.getChildActionsOrStubs()) {
            if (action is StepOutAction) {
                stepOut = action
            }
            if (action is ForceStepIntoAction) {
                forceStepInto = action
            }
        }
        topToolbar.remove(stepOut)
        topToolbar.remove(forceStepInto)

        historyManager.registerActions(topToolbar)
    }

    // Class' own methods
    public fun startTrace(line: String?) {
        historyManager.saveState()
        val context = getSession()!!.getSuspendContext()
        if (context != null) {
            contexts.add(context)
        }
        // disable actions
        debugger.trace(line)
    }

    public fun traceFinished() {
        if (historyManager.hasSavedStates()) {
            historyManager.loadState()
            if (!contexts.empty) {
                getSession()!!.positionReached(contexts.pollLast()!!)
            }
        } else if (stopAfterTrace) {
            getSession()!!.stop()
        } else {

        }
    }


    public fun isReadyForNextCommand(): Boolean = debugger.isReadyForNextCommand()

    public fun addExceptionBreakpoint(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        exceptionBreakpoint = breakpoint
        debugger.setExceptionBreakpoint(breakpoint.getProperties()!!.getState().exceptionType ==
                HaskellExceptionBreakpointProperties.ExceptionType.ERROR)
    }

    public fun removeExceptionBreakpoint(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        exceptionBreakpoint = null
        debugger.removeExceptionBreakpoint()
    }

    public fun setBreakpointNumberAtLine(breakpointNumber: Int, module: String, line: Int) {
        val entry = registeredBreakpoints.get(BreakpointPosition(module, line))
        if (entry != null) {
            entry.breakpointNumber = breakpointNumber
        }
    }

    public fun getBreakpointAtPosition(module: String, line: Int): XLineBreakpoint<XBreakpointProperties<*>>? {
        return registeredBreakpoints.get(BreakpointPosition(module, line))?.breakpoint
    }

    public fun addBreakpoint(module: String, line: Int, breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
        registeredBreakpoints.put(BreakpointPosition(module, line), BreakpointEntry(null, breakpoint))
        debugger.setBreakpoint(module, line)
    }

    public fun addBreakpointByIndex(module: String, index: Int, breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
        if (HaskellDebugSettings.getInstance().getState().debuggerType == HaskellDebugSettings.DebuggerType.REMOTE) {
            val line = HaskellUtils.zeroBasedToHaskellLineNumber(breakpoint.getLine())
            registeredBreakpoints.put(BreakpointPosition(module, line), BreakpointEntry(index, breakpoint))
            val command = SetBreakpointByIndexCommand(module, index, SetBreakpointCommand.StandardSetBreakpointCallback(module, debugRespondent))
            debugger.enqueueCommand(command)
        } else {
            throw RuntimeException(BREAK_BY_INDEX_ERROR_MSG)
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

    public fun syncBreakListForLine(moduleName: String, lineNumber: Int): ArrayList<BreakInfo> {
        if (HaskellDebugSettings.getInstance().getState().debuggerType == HaskellDebugSettings.DebuggerType.REMOTE) {
            val syncObject = SyncObject()
            val resultArray: ArrayList<BreakInfo> = ArrayList()
            val callback = BreakpointListCommand.DefaultCallback(resultArray)
            val command = BreakpointListCommand(moduleName, lineNumber, syncObject, callback)
            syncCommand(command, syncObject)
            return resultArray
        }
        return ArrayList()
    }

    private class BreakpointPosition(val module: String, val line: Int) {
        override fun equals(other: Any?): Boolean {
            if (other == null || other !is BreakpointPosition) {
                return false
            }
            return module.equals(other.module) && line.equals(other.line)
        }

        override fun hashCode(): Int = module.hashCode() * 31 + line
    }

    private class BreakpointEntry(var breakpointNumber: Int?, val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>)

    /**
     * Used to make synchronous requests to debugger.
     *
     * @see org.jetbrains.haskell.debugger.utils.SyncObject
     * @see org.jetbrains.haskell.debugger.HaskellDebugProcess#isReadyForNextCommand
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
}