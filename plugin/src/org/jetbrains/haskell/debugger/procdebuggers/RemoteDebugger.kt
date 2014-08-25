package org.jetbrains.haskell.debugger.procdebuggers

import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.jetbrains.haskell.debugger.parser.LocalBinding
import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import org.jetbrains.haskell.debugger.protocol.EvalCommand
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.parser.EvalResult
import com.intellij.execution.process.ProcessHandler
import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent

/**
 * Created by vlad on 7/30/14.
 */

public class RemoteDebugger(debugRespondent: DebugRespondent, debugProcessHandler: ProcessHandler)
: SimpleDebuggerImpl(debugRespondent, debugProcessHandler, null) {

    override val GLOBAL_BREAKPOINT_INDICES: Boolean = false

    override fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback) {
        val wrapperCallback = object : CommandCallback<EvalResult?>() {
            override fun execAfterParsing(result: EvalResult?) {
                val value = HsDebugValue(LocalBinding(null, result?.expressionType, result?.expressionValue))
                callback.evaluated(value)
            }
        }
        enqueueCommand(EvalCommand(false, expression, wrapperCallback))
    }

    override fun prepareDebugger() {}

    override fun updateBinding(binding: LocalBinding, lock: Lock, condition: Condition) {
        lock.lock()
        val callback = object : CommandCallback<EvalResult?>() {
            override fun execAfterParsing(result: EvalResult?) {
                lock.lock()
                binding.typeName = result?.expressionType
                binding.value = result?.expressionValue
                condition.signalAll()
                lock.unlock()
            }
        }
        enqueueCommand(EvalCommand(false, binding.name!!, callback))
        lock.unlock()
    }
}