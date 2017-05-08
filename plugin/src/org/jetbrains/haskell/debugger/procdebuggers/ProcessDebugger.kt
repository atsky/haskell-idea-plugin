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
import org.jetbrains.haskell.debugger.parser.ParseResult

interface ProcessDebugger {

    fun isReadyForNextCommand(): Boolean

    fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback)

    fun trace(line: String?)

    fun setBreakpoint(module: String, line: Int)

    fun removeBreakpoint(module: String, breakpointNumber: Int)

    fun setExceptionBreakpoint(uncaughtOnly: Boolean)

    fun removeExceptionBreakpoint()

    fun close()

    fun stepInto()

    fun stepOver()

    fun runToPosition(module: String, line: Int)

    fun resume()

    fun prepareDebugger()

    fun back(callback: CommandCallback<MoveHistResult?>?)

    fun forward(callback: CommandCallback<MoveHistResult?>?)

    fun print(binding: String, printCallback: CommandCallback<LocalBinding?>)

    fun force(binding: String, forceCallback: CommandCallback<LocalBinding?>)

    fun history(callback: CommandCallback<HistoryResult?>)

    fun updateBinding(binding: LocalBinding, lock: Lock, condition: Condition)

    fun enqueueCommand(command: AbstractCommand<*>)

    fun oldestExecutedCommand(): AbstractCommand<out ParseResult?>?

    fun removeOldestExecutedCommand()

    fun setReadyForInput()
}