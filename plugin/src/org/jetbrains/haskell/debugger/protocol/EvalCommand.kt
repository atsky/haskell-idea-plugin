package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ShowOutput
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.Parser
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import org.jetbrains.haskell.debugger.parser.LocalBinding
import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * Created by vlad on 8/1/14.
 */
public class EvalCommand(val expression: String, callback: CommandCallback<ParseResult?>)
: RealTimeCommand<ParseResult?>(callback) {

    override fun getBytes(): ByteArray = (":eval ${expression.trim()}\n").toByteArray()

    override fun parseGHCiOutput(output: Deque<String?>) = null
}