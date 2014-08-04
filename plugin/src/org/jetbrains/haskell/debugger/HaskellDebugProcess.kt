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
import org.jetbrains.haskell.debugger.highlighting.HsDebugSessionListener
import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.concurrent.locks.ReentrantLock
import org.jetbrains.haskell.debugger.protocol.ForceCommand
import org.jetbrains.haskell.debugger.config.HaskellDebugSettings
import com.intellij.xdebugger.ui.XDebugTabLayouter
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.content.Content
import com.intellij.ui.content.impl.ContentImpl
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import com.intellij.ui.AppUIUtil

/**
 * Created by vlad on 7/10/14.
 */

public class HaskellDebugProcess(session: XDebugSession,
                                 val executionConsole: ExecutionConsole,
                                 val myProcessHandler: HaskellDebugProcessHandler) : XDebugProcess(session), ProcessListener {

    private val debuggerEditorsProvider: XDebuggerEditorsProvider

    private val backAction: SwitchableAction = object : SwitchableAction("back", "Move back along history", com.intellij.icons.AllIcons.Actions.Back) {
        override fun actionPerformed(e: AnActionEvent?) {
            debugger.back()
        }
    }
    private val forwardAction: SwitchableAction = object : SwitchableAction("forward", "Move forward along history", com.intellij.icons.AllIcons.Actions.Forward) {
        override fun actionPerformed(e: AnActionEvent?) {
            debugger.forward()
        }
    }

    public val historyPanel: HistoryPanel = HistoryPanel(this)
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
        debugger.setBreakpoint(module, line)
    }

    public fun removeBreakpoint(module: String, line: Int) {
        val breakpointNumber: Int? = registeredBreakpoints.get(BreakpointPosition(module, line))?.breakpointNumber
        if (breakpointNumber != null) {
            registeredBreakpoints.remove(BreakpointPosition(module, line))
            debugger.removeBreakpoint(module, breakpointNumber)
        }
    }

    public fun fillFramesFromHistory(allHistFramesArray: ArrayList<HsCommonStackFrameInfo>,
                                     syncObject: Lock,
                                     frameIsFilled: Condition,
                                     frameHistoryIndex: Int) {
        debugger.backsSequence(SequenceOfBacksCommand(allHistFramesArray, syncObject, frameIsFilled, frameHistoryIndex,
                debugProcess = this))
    }

    public fun forceSetValue(localBinding: LocalBinding) {
        if(localBinding.name != null) {
            val syncObject: Lock = ReentrantLock()
            val bindingValueIsSet: Condition = syncObject.newCondition()
            val syncLocalBinding: LocalBinding = LocalBinding(localBinding.name, "", null)
            syncObject.lock()
            try {
                debugger.force(ForceCommand(localBinding.name!!,
                        ForceCommand.StandardForceCallback(syncLocalBinding, syncObject, bindingValueIsSet)))
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

        return object : XDebugTabLayouter() {

            override fun registerAdditionalContent(ui: RunnerLayoutUi) {
                val context = ui.createContent("history", historyPanel, "History", null, null)
                ui.addContent(context)
                ui.getContentManager()
            }
        }
    }

    override fun registerAdditionalActions(leftToolbar: DefaultActionGroup, topToolbar: DefaultActionGroup) {
        topToolbar.addSeparator()
        topToolbar.add(backAction)
        topToolbar.add(forwardAction)
    }

    public fun afterStopped(topHistory: Boolean, bottomHistory: Boolean, position: HsFilePosition) {
        AppUIUtil.invokeLaterIfProjectAlive(getSession()!!.getProject(), Runnable({() ->
            backAction.enabled = !bottomHistory
            forwardAction.enabled = !topHistory
            historyPanel.setCurrentSpan(position.toString())
        }))
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