package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent

/**
 * @author Habibullin Marat
 */

public class RemoveBreakpointCommand(val module: String?, val breakpointNumber: Int, callback: CommandCallback<Nothing?>?)
: RealTimeCommand<Nothing?>(callback) {

    override fun getText(): String = ":delete ${module ?: ""} $breakpointNumber\n"

    override fun parseGHCiOutput(output: Deque<String?>): Nothing? = null

    override fun parseJSONOutput(output: JSONObject): Nothing? = null

    public class StandardRemoveBreakpointCallback(val respondent: DebugRespondent) : CommandCallback<Nothing?>() {
        override fun execAfterParsing(result: Nothing?) {
            respondent.breakpointRemoved()
        }

    }
}
