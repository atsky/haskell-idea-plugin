package org.jetbrains.haskell.debugger

import com.intellij.execution.process.ProcessListener

/**
 * Created by vlad on 7/30/14.
 */

public class RemoteProcessHandler(process: Process, val streamHandler: RemoteDebugStreamHandler) : HaskellDebugProcessHandler(process: Process) {

    override fun setDebugProcessListener(listener: ProcessListener?) {
        streamHandler.listener = listener
    }

    override fun doDestroyProcess() {
        super<HaskellDebugProcessHandler>.doDestroyProcess()
        streamHandler.stop()
    }
}