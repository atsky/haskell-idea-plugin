package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * @author Habibullin Marat
 */

public class RemoveBreakpointCommand(val breakpointNumber: Int,
                                     callback: CommandCallback?) : RealTimeCommand(callback) {

    override fun getBytes(): ByteArray = ":delete $breakpointNumber\n".toByteArray()

    override fun parseOutput(output: Deque<String?>): ParseResult? = null
}
