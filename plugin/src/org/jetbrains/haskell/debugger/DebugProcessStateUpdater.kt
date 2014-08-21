package org.jetbrains.haskell.debugger

import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.config.HaskellDebugSettings

/**
 * @author Habibullin Marat
 */
public abstract class DebugProcessStateUpdater(protected val debugProcess: HaskellDebugProcess): ProcessListener {
    override fun startNotified(event: ProcessEvent?) { }

    override fun processTerminated(event: ProcessEvent?) { }

    override fun processWillTerminate(event: ProcessEvent?, willBeDestroyed: Boolean) { }
}

public class GHCiDebugProcessStateUpdater(debugProcess: HaskellDebugProcess): DebugProcessStateUpdater(debugProcess) {
    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        val oldestExecutedCommand = debugProcess.debugger.oldestExecutedCommand()

    }
}

public class RemoteDebugProcessStateUpdater(debugProcess: HaskellDebugProcess): DebugProcessStateUpdater(debugProcess) {
    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        val oldestExecutedCommand = debugProcess.debugger.oldestExecutedCommand()

    }
}