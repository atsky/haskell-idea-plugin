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

/**
 * Created by marat-x on 7/10/14.
 */
public class HaskellProgramRunner() : GenericProgramRunner<GenericDebuggerRunnerSettings>() {
    class object {
        public val HS_PROGRAM_RUNNER_ID: String = "HaskellProgramRunner"
    }

    override fun doExecute(project: Project, state: RunProfileState, contentToReuse: RunContentDescriptor?,
                           environment: ExecutionEnvironment): RunContentDescriptor?
    {
//        var haskellCmdLineState : HaskellCommandLineState = state as HaskellCommandLineState
//        var executionResult : ExecutionResult = haskellCmdLineState.execute(environment.getExecutor(), this)
//        haskellCmdLineState.getConsoleBuilder()?.getConsole()?.attachToProcess(executionResult.getProcessHandler())
        println("Debugger started")
        
        return null
    }
    override fun getRunnerId(): String = HS_PROGRAM_RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return true
    }

}