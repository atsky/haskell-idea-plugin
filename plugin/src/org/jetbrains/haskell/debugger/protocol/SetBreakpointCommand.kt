package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.GHCiParser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.JSONConverter

/**
 * @author Habibullin Marat
 */

public class SetBreakpointCommand(val module: String,
                                  val lineNumber: Int,
                                  callback: CommandCallback<BreakpointCommandResult?>?)
: RealTimeCommand<BreakpointCommandResult?>(callback) {

    override fun getText(): String = ":break $module $lineNumber\n"

    override fun parseGHCiOutput(output: Deque<String?>): BreakpointCommandResult? = GHCiParser.parseSetBreakpointCommandResult(output)

    override fun parseJSONOutput(output: JSONObject): BreakpointCommandResult? =
            if (JSONConverter.checkExceptionFromJSON(output) == null) JSONConverter.breakpointCommandResultFromJSON(output) else null

    class object {
        public class StandardSetBreakpointCallback(val module: String,
                                                   val debugProcess: HaskellDebugProcess)
                                                   : CommandCallback<BreakpointCommandResult?>() {
            override fun execAfterParsing(result: BreakpointCommandResult?) {
                if (result != null) {
                    debugProcess.setBreakpointNumberAtLine(result.breakpointNumber, module, result.position.rawStartLine)
                }
            }
        }
    }
}
