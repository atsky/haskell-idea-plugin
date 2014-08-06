package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.MoveHistResult
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.Parser
import org.json.simple.JSONObject

/**
 * Created by vlad on 8/4/14.
 */

public class ForwardCommand(callback: CommandCallback<MoveHistResult?>?) : RealTimeCommand<MoveHistResult?>(callback) {
    override fun getText(): String = ":forward\n"

    override fun parseGHCiOutput(output: Deque<String?>): MoveHistResult? = Parser.parseMoveHistResult(output)

    override fun parseJSONOutput(output: JSONObject): MoveHistResult? =
            if (Parser.checkExceptionFromJSON(output) == null) Parser.moveHistResultFromJSON(output) else null
}