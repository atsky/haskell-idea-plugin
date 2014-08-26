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

public class RemoteDebuggerTest : DebuggerTest<RemoteDebugger>() {

    class object {
        public class TestRemoteProcessHandler(process: Process, val streamHandler: RemoteDebugStreamHandler,
                                              listener: ProcessListener) : OSProcessHandler(process, null, null) {
            {
                streamHandler.processHandler = this
                streamHandler.listener = listener
            }

            override fun doDestroyProcess() {
                super.doDestroyProcess()
                streamHandler.stop()
            }
        }
    }

    override fun createDebugger(file: File, respondent: DebugRespondent): RemoteDebugger {
        val filePath = file.getAbsolutePath()

        val streamHandler = RemoteDebugStreamHandler()
        streamHandler.start()

        val debuggerPath = javaClass.getResource("/HaskellDebugger")?.getFile()
        assertNotNull(debuggerPath)

        val command: ArrayList<String> = arrayListOf(debuggerPath!!, "-m${filePath}", "-p${streamHandler.getPort()}")
        val builder = ProcessBuilder(command)
        val listener = RemoteDebugProcessStateUpdater()
        val handler = TestRemoteProcessHandler(builder.start(), streamHandler, listener)
        val debugger = RemoteDebugger(respondent, handler)
        listener.debugger = debugger
        return debugger
    }
}