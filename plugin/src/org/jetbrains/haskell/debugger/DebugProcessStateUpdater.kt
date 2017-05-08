package org.jetbrains.haskell.debugger

import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.procdebuggers.utils.InputReadinessChecker
import java.util.concurrent.atomic.AtomicBoolean
import com.intellij.execution.process.ProcessOutputTypes
import org.jetbrains.haskell.debugger.procdebuggers.GHCiDebugger
import org.jetbrains.haskell.debugger.protocol.RealTimeCommand
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger
import org.jetbrains.haskell.debugger.config.HaskellDebugSettings
import java.util.*

/**
 * @author Habibullin Marat
 */
abstract class DebugProcessStateUpdater : ProcessListener {
    protected val printDebuggerOutput: Boolean = HaskellDebugSettings.getInstance().state.printDebugOutput
    var debugger: ProcessDebugger? = null

    override fun startNotified(event: ProcessEvent?) { }

    override fun processTerminated(event: ProcessEvent?) { }

    override fun processWillTerminate(event: ProcessEvent?, willBeDestroyed: Boolean) { }

    abstract fun close()
}

class GHCiDebugProcessStateUpdater : DebugProcessStateUpdater() {
    private val inputReadinessChecker: InputReadinessChecker
    private var collectedOutput: StringBuilder = StringBuilder()

    val processStopped: AtomicBoolean = AtomicBoolean(false)

    init {
        inputReadinessChecker = InputReadinessChecker(this)
        inputReadinessChecker.start()
    }

    val INPUT_READINESS_PORT: Int = inputReadinessChecker.INPUT_READINESS_PORT

    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        val text = event?.text
        if (text != null) {
            if (printDebuggerOutput) {
                print(text)
            }
            if (outputType == ProcessOutputTypes.STDOUT) {
                collectedOutput.append(text)
                checkCollected()
            }
        }
    }

    fun checkCollected() {
        val oldestExecutedCommand = debugger?.oldestExecutedCommand()
        val outputIsDefinite = oldestExecutedCommand is RealTimeCommand
        if (simpleReadinessCheck() &&
                (processStopped.get() || !inputReadinessChecker.connected || outputIsDefinite)) {
            handleOutput(oldestExecutedCommand)
            processStopped.set(false)
            debugger?.setReadyForInput()
        }
    }

    override fun close() = inputReadinessChecker.stop()

    private fun simpleReadinessCheck(): Boolean = collectedOutput.toString().endsWith(GHCiDebugger.PROMPT_LINE)

    private fun handleOutput(oldestExecutedCommand: AbstractCommand<out ParseResult?>?) {
        oldestExecutedCommand?.handleGHCiOutput(LinkedList(collectedOutput.toString().split('\n')))
        collectedOutput = StringBuilder()
        debugger?.removeOldestExecutedCommand()
    }
}

class RemoteDebugProcessStateUpdater : DebugProcessStateUpdater() {
    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        val text = event?.text
        if (text != null) {
            if (printDebuggerOutput) {
                print(text)
            }
            val oldestExecutedCommand = debugger?.oldestExecutedCommand()
            oldestExecutedCommand?.handleJSONOutput(text)
            debugger?.removeOldestExecutedCommand()
            debugger?.setReadyForInput()
        }
    }

    override fun close() {
    }
}