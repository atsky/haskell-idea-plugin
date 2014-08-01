package org.jetbrains.haskell.debugger

import com.intellij.debugger.impl.GenericDebuggerRunnerSettings
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.openapi.project.Project
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.configurations.RunProfile
import org.jetbrains.haskell.run.haskell.HaskellCommandLineState
import com.intellij.execution.ExecutionResult
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugProcess
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.run.haskell.CabalRunConfiguration
import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.notification.NotificationType
import org.jetbrains.cabal.CabalInterface
import java.nio.charset.Charset
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.intellij.execution.ExecutionManager

/**
 * Class for starting debug session.
 *
 * @author Habibullin Marat
 */
public class HaskellProgramRunner() : GenericProgramRunner<GenericDebuggerRunnerSettings>() {
    class object {
        public val HS_PROGRAM_RUNNER_ID: String = "HaskellProgramRunner"

        private val ERROR_TITLE = "Debug execution error"
        private fun GENERAL_ERROR_MSG(projName: String) = "Internal error occured while executing debug process for ${projName}"
        private val MULTI_DEBUGGING_TITLE = "Can't start debugger"
        private val MULTI_DEBUGGING_MSG = "Haskell plugin supports only single debugging mode and one debugging " +
                                          "process is already in progress"
    }

    /**
     * Getter for this runner ID
     */
    override fun getRunnerId(): String = HS_PROGRAM_RUNNER_ID

    /**
     * Checks if this runner can be used with specified executor and RunProfile
     */
    override fun canRun(executorId: String, profile: RunProfile): Boolean =
            DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) && profile is CabalRunConfiguration

    /**
     * This method is executed when debug session is started (when you press "Debug" button)
     */
    override fun doExecute(project: Project, state: RunProfileState, contentToReuse: RunContentDescriptor?,
                           environment: ExecutionEnvironment): RunContentDescriptor?
    {
        val debuggerManager = XDebuggerManager.getInstance(project)
        if(debuggerManager == null) {
            Notifications.Bus.notify(Notification("", ERROR_TITLE, GENERAL_ERROR_MSG(project.getName()), NotificationType.ERROR))
            return null
        }
        try {
            if(debuggerManager.getDebugSessions().size != 0) {
                Notifications.Bus.notify(Notification("", MULTI_DEBUGGING_TITLE, MULTI_DEBUGGING_MSG, NotificationType.WARNING))
                focusDebugToolWindow(debuggerManager, project)
                return null
            }
            val executionResult = (state as HaskellCommandLineState).executeDebug(environment.getExecutor(), this)
            val processHandler = executionResult.getProcessHandler()!! as HaskellDebugProcessHandler

            val session = debuggerManager.startSession(this, environment, contentToReuse, object : XDebugProcessStarter() {
                        override fun start(session: XDebugSession): XDebugProcess =
                                HaskellDebugProcess(session, executionResult.getExecutionConsole()!!, processHandler)
                    })
            return session.getRunContentDescriptor()
        } catch (e: Exception) {
            val msg = GENERAL_ERROR_MSG(project.getName())
            Notifications.Bus.notify(Notification("", "Debug execution error", msg, NotificationType.ERROR))
            println("ERROR(HaskellProgramRunner.doExecute): ${e.getMessage()}")
            e.printStackTrace()
        }
        return null
    }

    private fun focusDebugToolWindow(debuggerManager: XDebuggerManager, project: Project) {
        val theOnlySession = debuggerManager.getDebugSessions().get(0)
        val descriptor = theOnlySession.getRunContentDescriptor()
        ExecutionManager.getInstance(project)!!.getContentManager().getToolWindowByDescriptor(descriptor)!!.show {}
    }
}