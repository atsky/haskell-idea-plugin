package org.jetbrains.haskell.debugger.prochandlers

import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessListener

abstract class HaskellDebugProcessHandler(process: Process): OSProcessHandler(process) {

    abstract fun setDebugProcessListener(listener: ProcessListener?)
}