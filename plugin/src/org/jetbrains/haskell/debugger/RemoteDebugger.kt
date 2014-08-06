package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import com.intellij.execution.ui.ConsoleViewContentType
import org.jetbrains.haskell.debugger.protocol.BreakpointListCommand
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

/**
 * Created by vlad on 7/30/14.
 */

public class RemoteDebugger(val debugProcess: HaskellDebugProcess) : ProcessDebugger {
    private val queue: CommandQueue
    private val writeLock = Any()

    private var lastCommand: AbstractCommand<out ParseResult?>? = null;

    {
        queue = CommandQueue({(command: AbstractCommand<out ParseResult?>) -> execute(command) })
        queue.start()
    }

    public var debugStarted: Boolean = false
        private set

    private fun execute(command: AbstractCommand<out ParseResult?>) {
        val text = command.getText()

        synchronized(writeLock) {
            lastCommand = command

            command.callback?.execBeforeSending()

            if (command !is HiddenCommand) {
                debugProcess.printToConsole(text, ConsoleViewContentType.SYSTEM_OUTPUT)
            }

            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(text.toByteArray())
            os.flush()

            if (command is TraceCommand) {
                debugStarted = true
            }
        }
    }

    override fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback) =
            queue.addCommand(EvalCommand(false, expression, object : CommandCallback<EvalResult?>() {
                override fun execAfterParsing(result: EvalResult?) {
                    callback.evaluated(HsDebugValue(LocalBinding(null, result?.expressionType, result?.expressionValue)))
                }
            }))

    override fun trace() =
            queue.addCommand(TraceCommand("main", FlowCommand.StandardFlowCallback(debugProcess)))

    override fun setBreakpoint(module: String, line: Int) =
            queue.addCommand(SetBreakpointCommand(module, line,
                    SetBreakpointCommand.StandardSetBreakpointCallback(module, debugProcess)))

    override fun removeBreakpoint(module: String, breakpointNumber: Int) =
            queue.addCommand(RemoveBreakpointCommand(module, breakpointNumber, null))

    override fun setExceptionBreakpoint(uncaughtOnly: Boolean) =
            queue.addCommand(HiddenCommand.createInstance(":set -fbreak-on-${if (uncaughtOnly) "error" else "exception"}\n"))

    override fun removeExceptionBreakpoint() {
        queue.addCommand(HiddenCommand.createInstance(":unset -fbreak-on-error\n"))
        queue.addCommand(HiddenCommand.createInstance(":unset -fbreak-on-exceptoion\n"))
    }

    override fun close() = queue.stop()

    override fun stepInto() = queue.addCommand(StepIntoCommand(StepCommand.StandardStepCallback(debugProcess)))

    override fun stepOver() = queue.addCommand(StepOverCommand(StepCommand.StandardStepCallback(debugProcess)))

    override fun runToPosition(module: String, line: Int) =
            queue.addCommand(SetBreakpointCommand(module, line,
                    object : CommandCallback<BreakpointCommandResult?>() {
                        override fun execAfterParsing(result: BreakpointCommandResult?) {
                        }
                    }))

    override fun resume() = queue.addCommand(ResumeCommand(FlowCommand.StandardFlowCallback(debugProcess)))

    override fun prepareDebugger() {
    }

    override fun back(backCommand: BackCommand) = queue.addCommand(backCommand)

    override fun forward() = queue.addCommand(ForwardCommand(null))

    override fun updateBinding(binding: LocalBinding, lock: Lock, condition: Condition) {
        lock.lock()
        queue.addCommand(EvalCommand(false, binding.name!!, object : CommandCallback<EvalResult?>() {
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

    override fun force(forceCommand: ForceCommand) = queue.addCommand(forceCommand)

    override fun history(callback: CommandCallback<HistoryResult?>) = queue.addCommand(HistoryCommand(callback))

    override fun print(printCommand: PrintCommand) = queue.addCommand(printCommand)

    override fun enqueueCommand(command: AbstractCommand<*>) = queue.addCommand(command)

    override fun onTextAvailable(text: String, outputType: Key<out Any?>?) {
        lastCommand?.handleJSONOutput(text)
        queue.setReadyForInput()
    }
}