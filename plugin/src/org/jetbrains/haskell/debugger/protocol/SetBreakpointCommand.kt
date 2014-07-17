package org.jetbrains.haskell.debugger.protocol

/**
 * @author Habibullin Marat
 */

public class SetBreakpointCommand(val lineNumber: Int) : AbstractCommand() {
    override fun getBytes(): ByteArray {
        return ":break $lineNumber\n".toByteArray()
    }
}
