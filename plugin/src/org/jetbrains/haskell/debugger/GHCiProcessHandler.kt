package org.jetbrains.haskell.debugger

import com.intellij.execution.process.ProcessListener

/**
 * Created by vlad on 7/30/14.
 */

public class GHCiProcessHandler(process: Process) : HaskellDebugProcessHandler(process: Process) {

    override fun setDebugProcessListener(listener: ProcessListener?) {
        addProcessListener(listener)
    }

}