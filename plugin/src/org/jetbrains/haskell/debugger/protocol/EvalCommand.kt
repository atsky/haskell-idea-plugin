package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ShowOutput
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.Parser
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import org.jetbrains.haskell.debugger.parser.LocalBinding
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.EvalResult

/**
 * Created by vlad on 8/1/14.
 */
public class EvalCommand(val expression: String, callback: CommandCallback<EvalResult?>)
: RealTimeCommand<EvalResult?>(callback) {

    override fun getText(): String = ":eval ${expression.trim()}\n"

    override fun parseGHCiOutput(output: Deque<String?>) = null

    override fun parseJSONOutput(output: JSONObject): EvalResult? =
            if (Parser.checkExceptionFromJSON(output) == null) Parser.evalResultFromJSON(output) else null
}