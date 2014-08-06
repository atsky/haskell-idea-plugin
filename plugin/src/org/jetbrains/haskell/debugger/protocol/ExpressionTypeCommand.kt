package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.ExpressionType
import org.json.simple.JSONObject

/**
 * Created by vlad on 7/23/14.
 */

public class ExpressionTypeCommand(val expression: String, callback: CommandCallback<ExpressionType?>?)
: RealTimeCommand<ExpressionType?>(callback) {
    override fun getText(): String = ":type $expression\n"

    override fun parseGHCiOutput(output: Deque<String?>): ExpressionType? = Parser.parseExpressionType(output.getFirst()!!)

    override fun parseJSONOutput(output: JSONObject): ExpressionType? {
        throw UnsupportedOperationException()
    }
}