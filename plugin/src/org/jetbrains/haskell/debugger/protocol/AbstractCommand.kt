package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.Deque

/**
 * Created by vlad on 7/10/14.
 */

public abstract class AbstractCommand() {

    public abstract fun getBytes(): ByteArray

    public abstract fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess)

}