package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.ExpressionType

/**
 * Created by vlad on 7/23/14.
 */

public class ExpressionTypeCommand(val expression: String, callback: CommandCallback?) : RealTimeCommand(callback) {
    override fun getBytes(): ByteArray {
        return ":type $expression\n".toByteArray()
    }

    override fun parseOutput(output: Deque<String?>): ParseResult? = Parser.parseExpressionType(output.getFirst()!!)
}