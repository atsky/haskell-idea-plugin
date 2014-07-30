package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XNamedValue
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import org.jetbrains.haskell.debugger.parser.LocalBinding
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.parser.ShowOutput

/**
 * Created by vlad on 7/23/14.
 */

public class ShowExpressionCommand(val expression: String, callback: CommandCallback<ShowOutput?>)
: RealTimeCommand<ShowOutput?>(callback) {

    /*
     * 'show' may be hidden
     */
    override fun getBytes(): ByteArray = ("Prelude.show (${expression.trim()})\n").toByteArray()

    override fun parseGHCiOutput(output: Deque<String?>): ShowOutput? = Parser.tryParseShowOutput(output)

    class object {
        public class StandardShowExpressionCallback(val expressionType: String?, val callback: XDebuggerEvaluator.XEvaluationCallback)
        : CommandCallback<ShowOutput?>() {
            override fun execAfterParsing(result: ShowOutput?) {
                if (result == null) {
                    callback.errorOccurred("Cannot show type: $expressionType")
                } else if (result is ShowOutput) {
                    callback.evaluated(HsDebugValue(LocalBinding(null, expressionType, result.output)))
                }
            }
        }
    }
}