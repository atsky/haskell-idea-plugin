package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult
import java.util.Deque
import org.json.simple.JSONObject
import org.apache.commons.lang.NotImplementedException

/**
 * Created by vlad on 7/31/14.
 */

public class BreakpointListCommand(val module: String, val lineToSet: Int? = null,
                                   callback: CommandCallback<ParseResult?>?) : RealTimeCommand<ParseResult?>(callback) {

    override fun getText(): String = ":breaklist ${module}\n"

    override fun parseGHCiOutput(output: Deque<String?>): ParseResult? {
        throw RuntimeException("Not supported in ghci")
    }

    override fun parseJSONOutput(output: JSONObject): ParseResult? {
        throw NotImplementedException()
    }
}