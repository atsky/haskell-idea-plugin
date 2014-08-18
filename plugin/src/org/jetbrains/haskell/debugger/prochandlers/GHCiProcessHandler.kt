package org.jetbrains.haskell.debugger.prochandlers

import com.intellij.execution.process.ProcessListener

public class GHCiProcessHandler(process: Process) : HaskellDebugProcessHandler(process: Process) {

    override fun setDebugProcessListener(listener: ProcessListener?) {
        addProcessListener(listener)
    }

}