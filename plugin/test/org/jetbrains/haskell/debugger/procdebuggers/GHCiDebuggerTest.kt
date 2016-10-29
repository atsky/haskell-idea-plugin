package org.jetbrains.haskell.debugger.procdebuggers

import java.io.File
import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent
import java.util.ArrayList
import com.intellij.execution.process.OSProcessHandler
import org.jetbrains.haskell.debugger.GHCiDebugProcessStateUpdater
import java.io.InputStreamReader
import com.intellij.openapi.vfs.CharsetToolkit
import java.io.InputStream
import com.intellij.execution.process.ProcessOutputTypes
import org.junit.Assert

public class GHCiDebuggerTest : DebuggerTest<GHCiDebugger>() {
    companion object {
        public val pathPropertyName: String = "ghciPath"

        public class TestGHCiProcessHandler(process: Process) : OSProcessHandler(process, null, CharsetToolkit.UTF8_CHARSET) {
            companion object {
                public class StreamReader(val stream: InputStream, val onTextAvailable: (String) -> Unit) : Thread() {
                    private var running: Boolean = true
                    private var reader: InputStreamReader? = null
                    override fun run() {
                        try {
                            reader = InputStreamReader(stream)
                            val buffer = CharArray(4096)
                            while (running) {
                                val sz = reader!!.read(buffer)
                                if (sz == -1) {
                                    return
                                }
                                onTextAvailable(String(buffer).substring(0, sz))
                            }
                        } catch (ex: Exception) {
                        }
                    }

                    public fun stopRunning() {
                        running = false
                        reader?.close()
                    }
                }
            }

            private val stdoutReader: StreamReader
            private val stderrReader: StreamReader

            init {
                stdoutReader = StreamReader(process.getInputStream()!!, { notifyTextAvailable(it, ProcessOutputTypes.STDOUT) })
                stderrReader = StreamReader(process.getErrorStream()!!, { notifyTextAvailable(it, ProcessOutputTypes.STDERR) })
                stdoutReader.start()
                stderrReader.start()
            }


            override fun doDestroyProcess() {
                stdoutReader.stopRunning()
                stderrReader.stopRunning()
                super.doDestroyProcess()
            }
        }
    }

    private var listener: GHCiDebugProcessStateUpdater? = null

    override fun createDebugger(file: File, respondent: DebugRespondent): GHCiDebugger {
        val filePath = file.getAbsolutePath()
        val ghciPath = DebuggerTest.properties?.getProperty(pathPropertyName)
        Assert.assertNotNull(ghciPath, "Path to ghci not found ($pathPropertyName property inside unittest.properties)")
        val command: ArrayList<String> = arrayListOf(ghciPath!!, filePath)
        command.add("-package")
        command.add("network")
        val process = Runtime.getRuntime().exec(command.toTypedArray())
        val handler = TestGHCiProcessHandler(process)
        listener = GHCiDebugProcessStateUpdater()
        val debugger = GHCiDebugger(respondent, handler, null, listener!!.INPUT_READINESS_PORT)
        listener!!.debugger = debugger
        handler.addProcessListener(listener)
        return debugger
    }

    override fun stopDebuggerServices() {
        listener?.close()
    }
}