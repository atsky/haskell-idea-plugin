package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * @author Habibullin Marat
 */

public class RemoveBreakpointCommand(val module: String?, val breakpointNumber: Int, callback: CommandCallback<ParseResult?>?)
: RealTimeCommand<ParseResult?>(callback) {

    override fun getText(): String = ":delete ${module ?: ""} $breakpointNumber\n"

    override fun parseGHCiOutput(output: Deque<String?>): ParseResult? = null
}
