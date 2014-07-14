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

/**
 * Class for starting debug session.
 *
 * @author Habibullin Marat
 */
public class HaskellProgramRunner() : GenericProgramRunner<GenericDebuggerRunnerSettings>() {
    class object {
        public val HS_PROGRAM_RUNNER_ID: String = "HaskellProgramRunner"
    }

    /**
     * Getter for this runner ID
     */
    override fun getRunnerId(): String = HS_PROGRAM_RUNNER_ID

    /**
     * Checks if this debugger can be used with specified executor and RunProfile
     */
    override fun canRun(executorId: String, profile: RunProfile): Boolean =
            DefaultDebugExecutor.EXECUTOR_ID.equals(executorId)

    /**
     * This method is executed when debug session is started (when you press "Debug" button)
     */
    override fun doExecute(project: Project, state: RunProfileState, contentToReuse: RunContentDescriptor?,
                           environment: ExecutionEnvironment): RunContentDescriptor?
    {
        val executionResult = (state as HaskellCommandLineState).executeDebug(environment.getExecutor(), this)
        val processHandler = executionResult.getProcessHandler()!!

        processHandler.addProcessListener(HaskellDebugProcessListener())

        val session = XDebuggerManager.getInstance(project)!!.
                startSession(this, environment, contentToReuse, object : XDebugProcessStarter() {
                    override fun start(session: XDebugSession): XDebugProcess =
                            GHCiDebugProcess(session, executionResult.getExecutionConsole()!!,  processHandler)
                })


        return session.getRunContentDescriptor()
    }
}