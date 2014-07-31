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
        try {
//            val hsCommandLineState = state as HaskellCommandLineState
//            val module = hsCommandLineState.configuration.getModule()
//            if (module != null) {
//                val cabalFile = CabalInterface.findCabal(module)

//                if(cabalFile != null) {
//                    val charset = cabalFile.getCharset()
//                    val executableName = (environment.getRunProfile() as CabalRunConfiguration).getMyExecutableName()
//                    if(charset != null && executableName != null) {
//                        val contents = cabalFile.contentsToByteArray().toString(charset).split('\n')
//                        val executablePattern = "executable\\s+(" + executableName + ")\\s*$"
//                        val execSectionStartIndex: Int = findFirstMatchInsensitive(executablePattern, contents)
//                        if(execSectionStartIndex != -1) {
//                            val mainIsPattern = "\\s*main-is:\\s+(\\w+\\.hs)\\s*$"
//                            val mainIsLineIndex = findFirstMatchInsensitive(mainIsPattern, contents, execSectionStartIndex)
//                            val
//                        }
//                    }
//                }
//            }

            val executionResult = (state as HaskellCommandLineState).executeDebug(environment.getExecutor(), this)
            val processHandler = executionResult.getProcessHandler()!! as HaskellDebugProcessHandler

            val session = XDebuggerManager.getInstance(project)!!.
                    startSession(this, environment, contentToReuse, object : XDebugProcessStarter() {
                        override fun start(session: XDebugSession): XDebugProcess =
                                HaskellDebugProcess(session, executionResult.getExecutionConsole()!!, processHandler)
                    })
            return session.getRunContentDescriptor()
        } catch (e: Exception) {
            val msg =  "Cannot execute debug process for ${project.getName()}. Check run configurations to try to fix the problem"
            Notifications.Bus.notify(Notification("", "Debug execution error", msg, NotificationType.ERROR))
            println("ERROR(HaskellProgramRunner.doExecute): ${e.getMessage()}")
            e.printStackTrace()
        }
        return null
    }
}