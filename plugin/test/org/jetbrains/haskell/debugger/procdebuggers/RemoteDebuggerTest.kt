package org.jetbrains.haskell.debugger.procdebuggers

import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent
import java.io.File
import com.intellij.execution.ExecutionException
import org.jetbrains.haskell.debugger.procdebuggers.utils.RemoteDebugStreamHandler
import org.jetbrains.haskell.debugger.config.HaskellDebugSettings
import java.util.ArrayList
import org.jetbrains.haskell.debugger.prochandlers.RemoteProcessHandler
import org.jetbrains.haskell.debugger.prochandlers.HaskellDebugProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessListener
import org.jetbrains.haskell.debugger.RemoteDebugProcessStateUpdater
import kotlin.test.assertNotNull
import org.jetbrains.haskell.debugger.GHCiDebugProcessStateUpdater

public class RemoteDebuggerTest : DebuggerTest<RemoteDebugger>() {

    companion object {
        public val pathPropertyName: String = "remotePath"

        public class TestRemoteProcessHandler(process: Process, val streamHandler: RemoteDebugStreamHandler,
                                              listener: ProcessListener) : OSProcessHandler(process, null, null) {
            init {
                streamHandler.processHandler = this
                streamHandler.listener = listener
            }

            override fun doDestroyProcess() {
                super.doDestroyProcess()
                streamHandler.stop()
            }
        }
    }

    private var listener: RemoteDebugProcessStateUpdater? = null

    override fun createDebugger(file: File, respondent: DebugRespondent): RemoteDebugger {
        val filePath = file.getAbsolutePath()

        val streamHandler = RemoteDebugStreamHandler()
        streamHandler.start()

        val debuggerPath = DebuggerTest.properties?.getProperty(pathPropertyName)
        assertNotNull(debuggerPath, "Path to remote debugger not found ($pathPropertyName property inside unittest.properties)")

        val command: ArrayList<String> = arrayListOf(debuggerPath!!, "-m${filePath}", "-p${streamHandler.getPort()}")
        val builder = ProcessBuilder(command)
        listener = RemoteDebugProcessStateUpdater()
        val handler = TestRemoteProcessHandler(builder.start(), streamHandler, listener!!)
        val debugger = RemoteDebugger(respondent, handler)
        listener!!.debugger = debugger
        return debugger
    }

    override fun stopDebuggerServices() {
        listener?.close()
    }
}