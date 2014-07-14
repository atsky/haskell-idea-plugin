package org.jetbrains.haskell.debugger.commands

/**
 * @author Habibullin Marat
 */

public class RemoveBreakpointCommand(val lineNumber: Int) : AbstractCommand("SetBreakpoint") {
    override fun getBytes(): ByteArray {
        return ":delete $lineNumber\n".toByteArray()
    }
}
