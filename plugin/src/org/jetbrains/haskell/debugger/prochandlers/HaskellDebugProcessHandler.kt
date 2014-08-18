package org.jetbrains.haskell.debugger.prochandlers

import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessListener

public abstract class HaskellDebugProcessHandler(process: Process): OSProcessHandler(process) {

    public abstract fun setDebugProcessListener(listener: ProcessListener?)
}