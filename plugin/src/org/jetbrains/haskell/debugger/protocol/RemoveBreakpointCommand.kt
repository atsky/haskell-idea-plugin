package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.Deque

/**
 * @author Habibullin Marat
 */

public class RemoveBreakpointCommand(val breakpointNumber: Int) : RealTimeCommand() {
    override fun getBytes(): ByteArray {
        return ":delete $breakpointNumber\n".toByteArray()
    }

    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {}
}
