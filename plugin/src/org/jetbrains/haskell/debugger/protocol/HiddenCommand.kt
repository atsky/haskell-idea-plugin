package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * Created by vlad on 7/17/14.
 */

public abstract class HiddenCommand
: AbstractCommand<ParseResult?>(null) {

    class object {
        public fun createInstance(command: String): HiddenCommand {
            return object : HiddenCommand() {
                override fun getText(): String = command
            }
        }
    }

    override fun parseGHCiOutput(output: Deque<String?>): ParseResult? = null
}
