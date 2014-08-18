package org.jetbrains.haskell.debugger.procdebuggers

import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.jetbrains.haskell.debugger.protocol.PrintCommand
import org.jetbrains.haskell.debugger.protocol.ForceCommand
import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import com.intellij.openapi.util.Key
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.jetbrains.haskell.debugger.parser.MoveHistResult
import org.jetbrains.haskell.debugger.parser.HistoryResult

public trait ProcessDebugger {

    public fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback)

    public fun trace()

    public fun setBreakpoint(module: String, line: Int)

    public fun removeBreakpoint(module: String, breakpointNumber: Int)

    public fun setExceptionBreakpoint(uncaughtOnly: Boolean)

    public fun removeExceptionBreakpoint()

    public fun close()

    public fun stepInto()

    public fun stepOver()

    public fun runToPosition(module: String, line: Int)

    public fun resume()

    public fun prepareDebugger()

    public fun back(callback: CommandCallback<MoveHistResult?>?)

    public fun forward(callback: CommandCallback<MoveHistResult?>?)

    public fun print(printCommand: PrintCommand)

    public fun force(forceCommand: ForceCommand)

    public fun history(callback: CommandCallback<HistoryResult?>)

    public fun updateBinding(binding: LocalBinding, lock: Lock, condition: Condition)

    public fun enqueueCommand(command: AbstractCommand<*>)

    public fun onTextAvailable(text: String, outputType: Key<out Any?>?)
}