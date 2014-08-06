package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ShowOutput
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.GHCiParser
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import org.jetbrains.haskell.debugger.parser.LocalBinding
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.EvalResult
import org.jetbrains.haskell.debugger.parser.JSONConverter

/**
 * Created by vlad on 8/1/14.
 */
public class EvalCommand(val force: Boolean, val expression: String, callback: CommandCallback<EvalResult?>)
: RealTimeCommand<EvalResult?>(callback) {

    override fun getText(): String = ":eval ${if (force) 1 else 0} ${expression.trim()}\n"

    override fun parseGHCiOutput(output: Deque<String?>) = null

    override fun parseJSONOutput(output: JSONObject): EvalResult? =
            if (JSONConverter.checkExceptionFromJSON(output) == null) JSONConverter.evalResultFromJSON(output) else null
}