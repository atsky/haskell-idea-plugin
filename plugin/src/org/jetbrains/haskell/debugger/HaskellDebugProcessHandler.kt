package org.jetbrains.haskell.debugger

import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessListener

/**
 * Created by vlad on 7/30/14.
 */

public abstract class HaskellDebugProcessHandler(process: Process): OSProcessHandler(process) {

    public abstract fun setDebugProcessListener(listener: ProcessListener?)
}