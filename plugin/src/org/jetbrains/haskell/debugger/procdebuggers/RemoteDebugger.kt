package org.jetbrains.haskell.debugger.procdebuggers

import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointCommand
import org.jetbrains.haskell.debugger.parser.LocalBinding
import org.jetbrains.haskell.debugger.protocol.ResumeCommand
import org.jetbrains.haskell.debugger.protocol.StepIntoCommand
import org.jetbrains.haskell.debugger.protocol.StepOverCommand
import org.jetbrains.haskell.debugger.protocol.RemoveBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult
import org.jetbrains.haskell.debugger.protocol.ForceCommand
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import org.jetbrains.haskell.debugger.protocol.EvalCommand
import org.jetbrains.haskell.debugger.protocol.PrintCommand
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.protocol.BackCommand
import org.jetbrains.haskell.debugger.parser.EvalResult
import org.jetbrains.haskell.debugger.protocol.FlowCommand
import org.jetbrains.haskell.debugger.protocol.StepCommand
import org.jetbrains.haskell.debugger.protocol.ForwardCommand
import org.jetbrains.haskell.debugger.parser.HistoryResult
import org.jetbrains.haskell.debugger.protocol.HistoryCommand
import org.jetbrains.haskell.debugger.parser.MoveHistResult
import org.jetbrains.haskell.debugger.procdebuggers.SimpleDebuggerImpl
import org.jetbrains.haskell.debugger.HaskellDebugProcess

/**
 * Created by vlad on 7/30/14.
 */

public class RemoteDebugger(debugProcess: HaskellDebugProcess) : SimpleDebuggerImpl(debugProcess, false) {

    override val TRACE_COMMAND: String = "main"
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

    override fun onTextAvailable(text: String, outputType: Key<out Any?>?) {
        lastCommand?.handleJSONOutput(text)
        setReadyForInput()
    }
}