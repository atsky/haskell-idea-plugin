package org.jetbrains.haskell.debugger.protocol

/**
 * @author Habibullin Marat
 */

public class RemoveBreakpointCommand(val lineNumber: Int) : AbstractCommand() {
    override fun getBytes(): ByteArray {
        return ":delete $lineNumber\n".toByteArray()
    }
}
