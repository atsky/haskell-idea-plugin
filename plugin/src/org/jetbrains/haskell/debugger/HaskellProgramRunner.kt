package org.jetbrains.haskell.debugger

import com.intellij.debugger.impl.GenericDebuggerRunnerSettings
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.openapi.project.Project
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.configurations.RunProfile
import org.jetbrains.haskell.run.haskell.HaskellCommandLineState
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugProcess
import com.intellij.execution.executors.DefaultDebugExecutor
import org.jetbrains.haskell.run.haskell.CabalRunConfiguration
import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.notification.NotificationType
import com.intellij.execution.ExecutionManager
import org.jetbrains.haskell.debugger.config.HaskellDebugSettings
import java.io.File
import com.intellij.notification.NotificationListener
import javax.swing.event.HyperlinkEvent
import com.intellij.openapi.options.ShowSettingsUtil
import org.jetbrains.haskell.debugger.prochandlers.HaskellDebugProcessHandler
import com.intellij.xdebugger.impl.XDebugSessionImpl
import org.jetbrains.haskell.debugger.config.DebuggerType

/**
 * Class for starting debug session.
 *
 * @author Habibullin Marat
 */
class HaskellProgramRunner : GenericProgramRunner<GenericDebuggerRunnerSettings>() {
    companion object {
        val HS_PROGRAM_RUNNER_ID: String = "HaskellProgramRunner"

        private val ERROR_TITLE = "Debug execution error"
        private fun GENERAL_ERROR_MSG(projName: String) = "Internal error occured while executing debug process for ${projName}"
        private val GENERAL_DEBUGGING_TITLE = "Can't start debugger"
        private val MULTI_DEBUGGING_MSG = "Haskell plugin supports only single debugging mode and one debugging " +
                "process is already in progress"
        private val WRONG_REMOTE_DEBUGGER_PATH_MSG = "Correct path to remote debugger was not set. See <a href=\"#\">Settings | Haskell Debugger</a>"
    }

    /**
     * Getter for this runner ID
     */
    override fun getRunnerId(): String = HS_PROGRAM_RUNNER_ID

    /**
     * Checks if this runner can be used with specified executor and RunProfile
     */
    override fun canRun(executorId: String, profile: RunProfile): Boolean =
            (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) || DebugConsoleExecutor.EXECUTOR_ID.equals(executorId)) &&
                    profile is CabalRunConfiguration

    /**
     * This method is executed when debug session is started (when you press "Debug" button)
     */
    override fun doExecute(project: Project, state: RunProfileState, contentToReuse: RunContentDescriptor?,
                           environment: ExecutionEnvironment): RunContentDescriptor? {
        val hyperlinkHandler = object : NotificationListener.Adapter() {
            override fun hyperlinkActivated(notification: Notification, e: HyperlinkEvent) {
                notification.expire()
                if (!project.isDisposed) {
                    ShowSettingsUtil.getInstance()?.showSettingsDialog(project, "Haskell Debugger")
                }
            }
        }
        val debuggerManager = XDebuggerManager.getInstance(project)
        if (debuggerManager == null) {
            Notifications.Bus.notify(Notification("", ERROR_TITLE, GENERAL_ERROR_MSG(project.name), NotificationType.ERROR))
            return null
        }
        try {
            if (debuggerManager.debugSessions.size != 0) {
                Notifications.Bus.notify(Notification("", GENERAL_DEBUGGING_TITLE, MULTI_DEBUGGING_MSG, NotificationType.WARNING))
                focusDebugToolWindow(debuggerManager, project)
                return null
            }
            val settingsState = HaskellDebugSettings.getInstance().state
            if (settingsState.debuggerType == DebuggerType.REMOTE) {
                if (settingsState.remoteDebuggerPath == null || !File(settingsState.remoteDebuggerPath!!).exists()) {
                    Notifications.Bus.notify(Notification("", GENERAL_DEBUGGING_TITLE, WRONG_REMOTE_DEBUGGER_PATH_MSG, NotificationType.WARNING, hyperlinkHandler))
                    return null
                }
            }
            val executionResult = (state as HaskellCommandLineState).executeDebug(project, environment.executor, this)
            val processHandler = executionResult.processHandler!! as HaskellDebugProcessHandler

            val session = debuggerManager.startSession(environment, object : XDebugProcessStarter() {
                override fun start(session: XDebugSession): XDebugProcess =
                        HaskellDebugProcess(session,
                                executionResult.executionConsole!!, processHandler,
                                DefaultDebugExecutor.EXECUTOR_ID.equals(environment.executor.id))
            })
            return session.runContentDescriptor
        } catch (e: Exception) {
            val msg = GENERAL_ERROR_MSG(project.name)
            Notifications.Bus.notify(Notification("", "Debug execution error", msg, NotificationType.ERROR))
        }
        return null
    }

    private fun focusDebugToolWindow(debuggerManager: XDebuggerManager, project: Project) {
        val theOnlySession = debuggerManager.debugSessions.get(0)
        val descriptor = theOnlySession.runContentDescriptor
        ExecutionManager.getInstance(project)!!.contentManager.getToolWindowByDescriptor(descriptor)!!.show {}
    }
}