package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult
import java.util.Deque

/**
 * Created by vlad on 7/31/14.
 */

public class BreakpointListCommand(val module: String, val lineToSet: Int? = null,
                                   callback: CommandCallback<ParseResult?>?) : RealTimeCommand<ParseResult?>(callback) {

    override fun getBytes(): ByteArray {
        return ":breaklist ${module}\n".getBytes()
    }

    override fun parseGHCiOutput(output: Deque<String?>): ParseResult? {
        throw RuntimeException("Not supported in ghci")
    }

}