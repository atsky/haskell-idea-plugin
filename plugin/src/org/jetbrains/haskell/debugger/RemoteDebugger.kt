package org.jetbrains.haskell.debugger

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

/**
 * Created by vlad on 7/30/14.
 */

public class RemoteDebugger(debugProcess: HaskellDebugProcess) : QueueDebugger(debugProcess) {

    override fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback) =
            enqueueCommand(EvalCommand(false, expression, object : CommandCallback<EvalResult?>() {
                override fun execAfterParsing(result: EvalResult?) {
                    callback.evaluated(HsDebugValue(LocalBinding(null, result?.expressionType, result?.expressionValue)))
                }
            }))

    override fun trace() =
            enqueueCommand(TraceCommand("main", FlowCommand.StandardFlowCallback(debugProcess)))

    override fun setBreakpoint(module: String, line: Int) =
            enqueueCommand(SetBreakpointCommand(module, line,
                    SetBreakpointCommand.StandardSetBreakpointCallback(module, debugProcess)))

    override fun removeBreakpoint(module: String, breakpointNumber: Int) =
            enqueueCommand(RemoveBreakpointCommand(module, breakpointNumber, null))

    override fun setExceptionBreakpoint(uncaughtOnly: Boolean) =
            enqueueCommand(HiddenCommand.createInstance(":set -fbreak-on-${if (uncaughtOnly) "error" else "exception"}\n"))

    override fun removeExceptionBreakpoint() {
        enqueueCommand(HiddenCommand.createInstance(":unset -fbreak-on-error\n"))
        enqueueCommand(HiddenCommand.createInstance(":unset -fbreak-on-exceptoion\n"))
    }

    override fun stepInto() = enqueueCommand(StepIntoCommand(StepCommand.StandardStepCallback(debugProcess)))

    override fun stepOver() = enqueueCommand(StepOverCommand(StepCommand.StandardStepCallback(debugProcess)))

    override fun runToPosition(module: String, line: Int) {
        if (debugProcess.getBreakpointAtPosition(module, line) == null) {
            enqueueCommand(SetBreakpointCommand(module, line, super.SetTempBreakForRunCallback("main\n", module)))
        } else {
            if (debugStarted) resume() else trace()
        }
    }

    override fun resume() = enqueueCommand(ResumeCommand(FlowCommand.StandardFlowCallback(debugProcess)))

    override fun prepareDebugger() {
    }

    override fun back(callback: CommandCallback<MoveHistResult?>?) =
            enqueueCommand(BackCommand(callback))

    override fun forward(callback: CommandCallback<MoveHistResult?>?) =
            enqueueCommand(ForwardCommand(callback))

    override fun updateBinding(binding: LocalBinding, lock: Lock, condition: Condition) {
        lock.lock()
        enqueueCommand(EvalCommand(false, binding.name!!, object : CommandCallback<EvalResult?>() {
            override fun execAfterParsing(result: EvalResult?) {
                lock.lock()
                binding.typeName = result?.expressionType
                binding.value = result?.expressionValue
                condition.signalAll()
                lock.unlock()
            }
        }))
        lock.unlock()
    }

    override fun force(forceCommand: ForceCommand) = enqueueCommand(forceCommand)

    override fun history(callback: CommandCallback<HistoryResult?>) = enqueueCommand(HistoryCommand(callback))

    override fun print(printCommand: PrintCommand) = enqueueCommand(printCommand)

    override fun onTextAvailable(text: String, outputType: Key<out Any?>?) {
        lastCommand?.handleJSONOutput(text)
        setReadyForInput()
    }
}