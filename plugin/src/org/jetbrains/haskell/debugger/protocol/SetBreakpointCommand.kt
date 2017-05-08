package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.GHCiParser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.JSONConverter
import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent

/**
 * @author Habibullin Marat
 */

class SetBreakpointCommand(val module: String,
                                  val lineNumber: Int,
                                  callback: CommandCallback<BreakpointCommandResult?>?)
: RealTimeCommand<BreakpointCommandResult?>(callback) {

    override fun getText(): String = ":break $module $lineNumber\n"

    override fun parseGHCiOutput(output: Deque<String?>): BreakpointCommandResult? = GHCiParser.parseSetBreakpointCommandResult(output)

    override fun parseJSONOutput(output: JSONObject): BreakpointCommandResult? =
            if (JSONConverter.checkExceptionFromJSON(output) == null) JSONConverter.breakpointCommandResultFromJSON(output) else null

    companion object {
        class StandardSetBreakpointCallback(val module: String,
                                                   val debugRespondent: DebugRespondent)
        : CommandCallback<BreakpointCommandResult?>() {
            override fun execAfterParsing(result: BreakpointCommandResult?) {
                if (result != null) {
                    debugRespondent.setBreakpointNumberAt(result.breakpointNumber, module, result.position.rawStartLine)
                }
            }
        }
    }
}

class SetBreakpointByIndexCommand(val module: String,
                                         val breakIndex: Int,
                                         callback: CommandCallback<BreakpointCommandResult?>?)
: RealTimeCommand<BreakpointCommandResult?>(callback) {

    override fun getText(): String = ":breakindex $module $breakIndex\n"

    override fun parseGHCiOutput(output: Deque<String?>): BreakpointCommandResult? {
        throw RuntimeException("Not supported by GHCi")
    }

    override fun parseJSONOutput(output: JSONObject): BreakpointCommandResult? =
            if (JSONConverter.checkExceptionFromJSON(output) == null) {
                JSONConverter.breakpointCommandResultFromJSON(output)
            } else {
                null
            }
}
