package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * Created by vlad on 7/17/14.
 */

public abstract class HiddenCommand : AbstractCommand(null) {

    class object {
        public fun createInstance(command: String): HiddenCommand {
            return object : HiddenCommand() {
                override fun getBytes(): ByteArray = command.toByteArray()
            }
        }
    }

    override fun parseOutput(output: Deque<String?>): ParseResult? = null
}
