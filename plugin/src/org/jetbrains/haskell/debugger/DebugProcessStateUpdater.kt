package org.jetbrains.haskell.debugger

import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.config.HaskellDebugSettings
import org.jetbrains.haskell.debugger.procdebuggers.utils.InputReadinessChecker
import java.util.concurrent.atomic.AtomicBoolean
import com.intellij.execution.process.ProcessOutputTypes
import org.jetbrains.haskell.debugger.procdebuggers.GHCiDebugger
import org.jetbrains.haskell.debugger.protocol.RealTimeCommand
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.protocol.AbstractCommand

/**
 * @author Habibullin Marat
 */
public abstract class DebugProcessStateUpdater(protected val debugProcess: HaskellDebugProcess): ProcessListener {
    override fun startNotified(event: ProcessEvent?) { }

    override fun processTerminated(event: ProcessEvent?) { }

    override fun processWillTerminate(event: ProcessEvent?, willBeDestroyed: Boolean) { }

    public abstract fun close()
}

public class GHCiDebugProcessStateUpdater(debugProcess: HaskellDebugProcess): DebugProcessStateUpdater(debugProcess) {
    private val inputReadinessChecker: InputReadinessChecker
    private var collectedOutput: StringBuilder = StringBuilder()

    public val processStopped: AtomicBoolean = AtomicBoolean(false);

    {
        inputReadinessChecker = InputReadinessChecker(this, { })
        inputReadinessChecker.start()
    }

    public val INPUT_READINESS_PORT: Int = inputReadinessChecker.INPUT_READINESS_PORT

    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        val text = event?.getText()
        if (text != null) {
            print(text)
            if (outputType == ProcessOutputTypes.STDOUT) {
                val oldestExecutedCommand = debugProcess.debugger.oldestExecutedCommand()
                val outputIsDefinite = oldestExecutedCommand is RealTimeCommand
                collectedOutput.append(text)
                if (simpleReadinessCheck() &&
                        (processStopped.get() || !inputReadinessChecker.connected || outputIsDefinite)) {
                    handleOutput(oldestExecutedCommand)
                    processStopped.set(false)
                    debugProcess.debugger.setReadyForInput()
                }
            }
        }
    }

    override fun close() = inputReadinessChecker.stop()

    private fun simpleReadinessCheck(): Boolean = collectedOutput.toString().endsWith(GHCiDebugger.PROMPT_LINE)

    private fun handleOutput(oldestExecutedCommand: AbstractCommand<out ParseResult?>?) {
        oldestExecutedCommand?.handleGHCiOutput(collectedOutput.toString().split('\n').toLinkedList())
        collectedOutput = StringBuilder()
        debugProcess.debugger.removeOldestExecutedCommand()
    }
}

public class RemoteDebugProcessStateUpdater(debugProcess: HaskellDebugProcess): DebugProcessStateUpdater(debugProcess) {
    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        val text = event?.getText()
        if (text != null) {
            print(text)
            val oldestExecutedCommand = debugProcess.debugger.oldestExecutedCommand()
            oldestExecutedCommand?.handleJSONOutput(text)
            debugProcess.debugger.removeOldestExecutedCommand()
            debugProcess.debugger.setReadyForInput()
        }
    }

    override fun close() { }
}