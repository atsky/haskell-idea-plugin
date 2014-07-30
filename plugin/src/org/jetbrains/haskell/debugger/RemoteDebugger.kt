package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.protocol.SequenceOfBacksCommand
import org.jetbrains.haskell.debugger.protocol.SequenceOfForwardsCommand
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import org.jetbrains.haskell.debugger.protocol.FlowCommand
import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.codehaus.jettison.json.JSONObject

/**
 * Created by vlad on 7/30/14.
 */

public class RemoteDebugger(val debugProcess: HaskellDebugProcess) : ProcessDebugger {

    private val queue: CommandQueue
    private val writeLock = Any();

    {
        queue = CommandQueue({(command: AbstractCommand<out ParseResult?>) -> execute(command) })
        queue.start()
    }

    public var debugStarted: Boolean = false
        private set

    private fun execute(command: AbstractCommand<out ParseResult?>) {
        val bytes = command.getBytes()

        synchronized(writeLock) {
            if (command !is HiddenCommand) {
                debugProcess.printToConsole(String(bytes))

                System.out.write(bytes)
                System.out.flush()
            }

            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(bytes)
            os.flush()

            if (command is TraceCommand) {
                debugStarted = true
            }
        }
    }

    override fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback) {
        throw UnsupportedOperationException()
    }

    override fun trace() {
        queue.addCommand(TraceCommand("main",
                FlowCommand.StandardFlowCallback(debugProcess)))
    }

    override fun setBreakpoint(module: String, line: Int) {
        throw UnsupportedOperationException()
    }

    override fun removeBreakpoint(breakpointNumber: Int) {
        throw UnsupportedOperationException()
    }

    override fun close() {
        queue.stop()
    }

    override fun stepInto() {
        throw UnsupportedOperationException()
    }

    override fun stepOver() {
        throw UnsupportedOperationException()
    }

    override fun runToPosition(module: String, line: Int) {
        throw UnsupportedOperationException()
    }

    override fun resume() {
        throw UnsupportedOperationException()
    }

    override fun prepareGHCi() {
        throw UnsupportedOperationException()
    }

    override fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>?, topFrameInfo: HsTopStackFrameInfo) {
        throw UnsupportedOperationException()
    }

    override fun backsSequence(sequenceOfBacksCommand: SequenceOfBacksCommand) {
        throw UnsupportedOperationException()
    }

    override fun forwardsSequence(sequenceOfForwardsCommand: SequenceOfForwardsCommand) {
        throw UnsupportedOperationException()
    }

    override fun onTextAvailable(text: String, outputType: Key<out Any?>?) {
        throw UnsupportedOperationException()
    }

    public inner class JSONHandler {
        public fun handle(result: JSONObject) {
            val info = result.getString("info")
            when (info) {
                null -> {
                    throw RuntimeException("Missing data type")
                }
                "finished" -> {
                    debugProcess.getSession()!!.stop()
                }
                else -> {
                    throw RuntimeException("Unknown result")
                }
            }
        }
    }

}