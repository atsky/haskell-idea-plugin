package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.RemoveBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.StepIntoCommand
import org.jetbrains.haskell.debugger.protocol.StepOverCommand
import org.jetbrains.haskell.debugger.protocol.ResumeCommand
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import org.jetbrains.haskell.debugger.protocol.HistoryCommand
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.protocol.RealTimeCommand
import com.intellij.openapi.util.Key
import com.intellij.execution.process.ProcessOutputTypes
import java.util.concurrent.atomic.AtomicBoolean
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.protocol.SequenceOfBacksCommand
import org.jetbrains.haskell.debugger.protocol.SequenceOfForwardsCommand
import org.jetbrains.haskell.debugger.protocol.FlowCommand
import org.jetbrains.haskell.debugger.protocol.StepCommand
import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.jetbrains.haskell.debugger.protocol.ExpressionTypeCommand
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.ExpressionType
import org.jetbrains.haskell.debugger.protocol.ShowExpressionCommand
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult
import com.intellij.xdebugger.frame.XSuspendContext

/**
 * Created by vlad on 7/11/14.
 */

public class GHCiDebugger(val debugProcess: HaskellDebugProcess) : ProcessDebugger {

    class object {
        private val HANDLE_NAME = "handle"
        private val TRACE_CMD = "main >> (withSocketsDo $ $HANDLE_NAME >>= \\ h -> hPutChar h (chr 1) >> hClose h)"
        public val PROMPT_LINE: String = "debug> "
    }

    private val inputReadinessChecker: InputReadinessChecker
    private var collectedOutput: StringBuilder = StringBuilder()
    private val queue: CommandQueue
    private val writeLock = Any()

    public val processStopped: AtomicBoolean = AtomicBoolean(false)

    public var lastCommand: AbstractCommand? = null;

    {
        queue = CommandQueue({(command: AbstractCommand) -> execute(command) })
        queue.start()

        inputReadinessChecker = InputReadinessChecker(this, {() -> onStopSignal() })
        inputReadinessChecker.start()
    }
    public var debugStarted: Boolean = false
        private set

    override fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback) {
        queue.addCommand(ExpressionTypeCommand(expression, object : CommandCallback() {
            override fun execAfterParsing(result: ParseResult?) {
                if (result == null) {
                    callback.errorOccurred("Unknown expression")
                } else if (result is ExpressionType) {
                    val expType = result.expressionType
                    queue.addCommand(ShowExpressionCommand(expression,
                            ShowExpressionCommand.StandardShowExpressionCallback(expType, callback)))
                }
            }
        }))
    }

    override fun trace() {
        queue.addCommand(TraceCommand(TRACE_CMD,
                FlowCommand.StandardFlowCallback(debugProcess)))
    }

    /**
     * Executes command immediately
     */
    private fun execute(command: AbstractCommand) {
        val bytes = command.getBytes()

        synchronized(writeLock) {
            lastCommand = command

            if (lastCommand !is HiddenCommand) {
                debugProcess.printToConsole(String(bytes))

                System.out.write(bytes)
                System.out.flush()
            }

            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(bytes)
            os.flush()

            if (lastCommand is TraceCommand) {
                debugStarted = true
            }
        }
    }

    override fun setBreakpoint(line: Int) = queue.addCommand(SetBreakpointCommand(line,
            SetBreakpointCommand.StandardSetBreakpointCallback(debugProcess)))

    override fun removeBreakpoint(breakpointNumber: Int) = queue.addCommand(RemoveBreakpointCommand(breakpointNumber, null))

    override fun stepInto() {
        queue.addCommand(StepIntoCommand(StepCommand.StandardStepCallback(debugProcess)))
    }

    override fun stepOver() {
        queue.addCommand(StepOverCommand(StepCommand.StandardStepCallback(debugProcess)))
    }

    override fun runToPosition(line: Int) {
        if (debugProcess.getBreakpointAtLine(line) == null) {
            queue.addCommand(SetBreakpointCommand(line, RunToPositionCallback(line)))
        } else {
            if (debugStarted) resume() else trace()
        }
    }

    override fun resume() {
        queue.addCommand(ResumeCommand(FlowCommand.StandardFlowCallback(debugProcess)))
    }

    override fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?, topFrameInfo: HsTopStackFrameInfo) {
        queue.addCommand(HistoryCommand(HistoryCommand.StandardHistoryCallback(breakpoint, topFrameInfo, debugProcess)))
    }

    override public fun backsSequence(sequenceOfBacksCommand: SequenceOfBacksCommand) {
        queue.addCommand(sequenceOfBacksCommand)
    }
    override public fun forwardsSequence(sequenceOfForwardsCommand: SequenceOfForwardsCommand) {
        queue.addCommand(sequenceOfForwardsCommand)
    }

    override fun prepareGHCi() {
        execute(HiddenCommand.createInstance(":set prompt \"$PROMPT_LINE\"\n"))

        val connectTo_host_port = "\\host port_ -> let port = toEnum port_ in " +
                "socket AF_INET Stream 0 >>= " +
                "(\\sock -> liftM hostAddresses (getHostByName host) >>= " +
                "(\\addrs -> connect sock (SockAddrInet port (head addrs)) >> " +
                "socketToHandle sock ReadWriteMode >>=  " +
                "(\\handle -> return handle)))"
        val host = "\"localhost\""
        val port = HaskellDebugProcess.INPUT_READINESS_PORT
        var stop_cmd = "withSocketsDo $ $HANDLE_NAME >>= \\ h -> hPutChar h (chr 0) >> hClose h"

        /*
         * todo:
         * 1. need to be careful with concurrency of modules
         * 2. handle name can be used
         */
        val commands = array(
                ":m +System.IO\n",
                ":m +Data.Char\n",
                ":m +Network.Socket\n",
                ":m +Network.BSD\n",
                ":m +Control.Monad\n",
                ":m +Control.Concurrent\n",
                "let $HANDLE_NAME = ($connectTo_host_port) $host $port\n",
                ":set stop $stop_cmd\n"
        )
        for (cmd in commands) {
            queue.addCommand(HiddenCommand.createInstance(cmd), highPriority = true)
        }
    }

    override fun close() {
        inputReadinessChecker.stop()
        queue.stop()
    }

    override fun onTextAvailable(text: String, outputType: Key<out Any?>?) {
        if (outputType != ProcessOutputTypes.SYSTEM) {
            collectedOutput.append(text)
            if (simpleReadinessCheck() &&
                    (processStopped.get() || !inputReadinessChecker.connected || outputIsDefinite())) {
                handleOutput()
                processStopped.set(false)
                setReadyForInput()
            }
        }
    }

    private fun setReadyForInput() {
        queue.setReadyForInput()
    }

    private fun handleOutput() {
        lastCommand?.handleOutput(collectedOutput.toString().split('\n').toLinkedList())
        collectedOutput = StringBuilder()
    }

    private fun outputIsDefinite(): Boolean {
        return lastCommand is RealTimeCommand
    }

    private fun simpleReadinessCheck(): Boolean = collectedOutput.toString().endsWith(PROMPT_LINE)

    private fun onStopSignal() {
        debugProcess.getSession()?.stop()
    }


    private inner class RunToPositionCallback(val line: Int): CommandCallback() {
        // Was unable to define enum class here
        private var state: Int = 0
        private var breakpointNumber: Int? = null
        private var flowResult: HsTopStackFrameInfo? = null

        override fun execAfterParsing(result: ParseResult?) {
            when (state) {
                0 -> { // Run to temporary breakpoint
                    state++
                    if (result == null) {
                        return
                    } else if (result is BreakpointCommandResult) {
                        breakpointNumber = result.breakpointNumber
                    } else {
                        throw RuntimeException("Wrong result obtained while setting a temporary breakpoint")
                    }
                    if (debugStarted) {
                        queue.addCommand(ResumeCommand(this), true)
                    } else {
                        queue.addCommand(TraceCommand(TRACE_CMD, this), true)
                    }
                }
                1 -> { // Remove temporary breakpoint
                    state++
                    if (result != null && result is HsTopStackFrameInfo) {
                        flowResult = result
                    } else {
                        throw RuntimeException("Wrong result obtained while running to the temporary breakpoint")
                    }
                    queue.addCommand(RemoveBreakpointCommand(breakpointNumber!!, this), true)
                }
                2 -> { // Finish
                    if (flowResult != null) {
                        history(null, flowResult!!)
                    }
                }
            }
        }
    }
}