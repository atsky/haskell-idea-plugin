package org.jetbrains.haskell.debugger.frames

import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.XSourcePosition
import org.jetbrains.haskell.debugger.ProcessDebugger
//import org.jetbrains.haskell.debugger.protocol.ExpressionCommand
import com.intellij.xdebugger.frame.XNamedValue

/**
 * Created by vlad on 7/23/14.
 */

public class HsDebuggerEvaluator (val debugger: ProcessDebugger): XDebuggerEvaluator() {

    override fun evaluate(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback, expressionPosition: XSourcePosition?) {
        debugger.evaluateExpression(expression, callback)
    }
}