package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess

/**
 * @author Habibullin Marat
 */
public class SequenceOfBacksCommand(): RealTimeCommand() {
    override fun getBytes(): ByteArray {
        throw UnsupportedOperationException()
    }
    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        throw UnsupportedOperationException()
    }

}