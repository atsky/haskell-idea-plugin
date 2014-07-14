package org.jetbrains.haskell.debugger

import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by vlad on 7/14/14.
 */

public class HaskellDebugProcessListener() : ProcessListener {

    public val ready: AtomicBoolean = AtomicBoolean(false)

    override fun startNotified(event: ProcessEvent?) {
    }
    override fun processTerminated(event: ProcessEvent?) {
    }
    override fun processWillTerminate(event: ProcessEvent?, willBeDestroyed: Boolean) {
    }
    override fun onTextAvailable(event: ProcessEvent?, outputType: Key<out Any?>?) {
        print(event?.getText())
        if (isReadyForInput(event?.getText())) {
            ready.set(true)
        }
    }

    private fun isReadyForInput(line: String?): Boolean = "*Main>".equals(line?.trim())    //temporary

}