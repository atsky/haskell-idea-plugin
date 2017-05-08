package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.MoveHistResult
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.GHCiParser
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.JSONConverter

/**
 * Created by vlad on 8/4/14.
 */

class ForwardCommand(callback: CommandCallback<MoveHistResult?>?) : RealTimeCommand<MoveHistResult?>(callback) {
    override fun getText(): String = ":forward\n"

    override fun parseGHCiOutput(output: Deque<String?>): MoveHistResult? = GHCiParser.parseMoveHistResult(output)

    override fun parseJSONOutput(output: JSONObject): MoveHistResult? =
            if (JSONConverter.checkExceptionFromJSON(output) == null) JSONConverter.moveHistResultFromJSON(output) else null
}