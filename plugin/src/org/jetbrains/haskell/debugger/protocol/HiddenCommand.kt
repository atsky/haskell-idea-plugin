package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.Deque

/**
 * Created by vlad on 7/17/14.
 */

public abstract class HiddenCommand : AbstractCommand() {


    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
    }
}
