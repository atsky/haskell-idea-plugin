package org.jetbrains.haskell.debugger.commands

/**
 * @author Habibullin Marat
 */

public class SetBreakpointCommand(val lineNumber: Int) : AbstractCommand() {
    override fun getBytes(): ByteArray {
        return ":break $lineNumber\n".toByteArray()
    }
}
