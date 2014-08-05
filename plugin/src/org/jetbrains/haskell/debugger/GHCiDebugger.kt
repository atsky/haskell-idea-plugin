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
import com.intellij.execution.ui.ConsoleViewContentType
import org.jetbrains.haskell.debugger.protocol.ForceCommand
import org.jetbrains.haskell.debugger.protocol.BackCommand
import org.jetbrains.haskell.debugger.protocol.ForwardCommand
import org.jetbrains.haskell.debugger.parser.History
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.parser.MoveHistResult
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import org.jetbrains.haskell.debugger.frames.HsTopStackFrame

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

    private var historySize = 0
    private var historyIndex = 0

    public val processStopped: AtomicBoolean = AtomicBoolean(false)

    private var lastCommand: AbstractCommand<out ParseResult?>? = null;

    {
        queue = CommandQueue({(command: AbstractCommand<out ParseResult?>) -> execute(command) })
        queue.start()

        inputReadinessChecker = InputReadinessChecker(this, {() -> onStopSignal() })
        inputReadinessChecker.start()
    }
    public var debugStarted: Boolean = false
        private set

    override fun evaluateExpression(expression: String, callback: XDebuggerEvaluator.XEvaluationCallback) {
        queue.addCommand(ExpressionTypeCommand(expression, object : CommandCallback<ExpressionType?>() {
            override fun execAfterParsing(result: ExpressionType?) {
                if (result == null) {
                    callback.errorOccurred("Unknown expression")
                } else {
                    val expType = result.expressionType
                    queue.addCommand(ShowExpressionCommand(expression,
                            ShowExpressionCommand.StandardShowExpressionCallback(expType, callback)))
                }
            }
        }))
    }

    override fun trace() =
            queue.addCommand(TraceCommand(TRACE_CMD, FlowCommand.StandardFlowCallback(debugProcess)))

    /**
     * Executes command immediately
     */
    private fun execute(command: AbstractCommand<out ParseResult?>) {
        val bytes = command.getBytes()

        synchronized(writeLock) {
            lastCommand = command

            if (lastCommand !is HiddenCommand) {
                debugProcess.printToConsole(String(bytes), ConsoleViewContentType.SYSTEM_OUTPUT)
            }

            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(bytes)
            os.flush()

            if (lastCommand is TraceCommand) {
                debugStarted = true
            }
        }
    }

    override fun setBreakpoint(module: String, line: Int) = queue.addCommand(SetBreakpointCommand(module, line,
            SetBreakpointCommand.StandardSetBreakpointCallback(module, debugProcess)))

    override fun removeBreakpoint(module: String, breakpointNumber: Int) = queue.addCommand(RemoveBreakpointCommand(null, breakpointNumber, null))

    override fun stepInto() =
            queue.addCommand(StepIntoCommand(StepCommand.StandardStepCallback(debugProcess)))

    override fun stepOver() =
            queue.addCommand(StepOverCommand(StepCommand.StandardStepCallback(debugProcess)))

    override fun runToPosition(module: String, line: Int) {
        if (debugProcess.getBreakpointAtPosition(module, line) == null) {
            queue.addCommand(SetBreakpointCommand(module, line, SetTempBreakCallback()))
        } else {
            if (debugStarted) resume() else trace()
        }
    }

    override fun resume() =
            queue.addCommand(ResumeCommand(FlowCommand.StandardFlowCallback(debugProcess)))

    override fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?, topFrameInfo: HsTopStackFrameInfo) =
            queue.addCommand(HistoryCommand(StandardHistoryCallback(breakpoint, topFrameInfo)))

    override fun back() {
        if (historyIndex + 1 < historySize) {
            ++historyIndex
            queue.addCommand(BackCommand(StandardMoveHistCallback()))
        }
    }

    override fun forward() {
        if (historyIndex > 0) {
            --historyIndex
            queue.addCommand(ForwardCommand(StandardMoveHistCallback()))
        }
    }

    override public fun backsSequence(sequenceOfBacksCommand: SequenceOfBacksCommand) =
            queue.addCommand(sequenceOfBacksCommand)

    override public fun forwardsSequence(sequenceOfForwardsCommand: SequenceOfForwardsCommand) =
            queue.addCommand(sequenceOfForwardsCommand)

    override fun force(forceCommand: ForceCommand) = queue.addCommand(forceCommand)

    override fun sequenceCommand(command: AbstractCommand<*>, length: Int) {
        for(i in 0..length) {
            queue.addCommand(command)
        }
    }

    override fun prepareDebugger() {
        execute(HiddenCommand.createInstance(":set prompt \"$PROMPT_LINE\"\n"))

        val connectTo_host_port = "\\host port_ -> let port = toEnum port_ in " +
                "socket AF_INET Stream 0 >>= " +
                "(\\sock -> liftM hostAddresses (getHostByName host) >>= " +
                "(\\addrs -> connect sock (SockAddrInet port (head addrs)) >> " +
                "socketToHandle sock ReadWriteMode >>=  " +
                "(\\handle -> return handle)))"
        val host = "\"localhost\""
        val port = inputReadinessChecker.INPUT_READINESS_PORT
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

    private inner class SetTempBreakCallback() : CommandCallback<BreakpointCommandResult?>() {
        override fun execAfterParsing(result: BreakpointCommandResult?) {
            if (result == null) {
                return
            }
            if (debugStarted) {
                queue.addCommand(ResumeCommand(RunToPositionCallback(result.breakpointNumber)), true)
            } else {
                queue.addCommand(TraceCommand(TRACE_CMD, RunToPositionCallback(result.breakpointNumber)), true)
            }
        }
    }

    private inner class RunToPositionCallback(val breakpointNumber: Int) : CommandCallback<HsTopStackFrameInfo?>() {
        override fun execAfterParsing(result: HsTopStackFrameInfo?) {
            if (result == null) {
                throw RuntimeException("Wrong result obtained while running to the temporary breakpoint")
            }
            queue.addCommand(RemoveBreakpointCommand(null, breakpointNumber, RemoveTempBreakCallback(result)), true)
        }
    }

    private inner class RemoveTempBreakCallback(val flowResult: HsTopStackFrameInfo)
    : CommandCallback<ParseResult?>() {
        override fun execAfterParsing(result: ParseResult?) = history(null, flowResult)
    }

    private inner class StandardHistoryCallback(val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?,
                                                val topFrameInfo: HsTopStackFrameInfo)
    : CommandCallback<History?>() {
        override fun execAfterParsing(result: History?) {
            if (result != null && result is History) {
                val histFrames = result.list
                val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", topFrameInfo, histFrames))
                historySize = result.list.size + 1
                historyIndex = 0
                debugProcess.historyChanged(true, histFrames.empty, HsTopStackFrame(debugProcess, topFrameInfo))
                if (breakpoint != null) {
                    debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
                } else {
                    debugProcess.getSession()!!.positionReached(context)
                }
            }
        }
    }

    private inner class StandardMoveHistCallback() : CommandCallback<MoveHistResult?>() {
        override fun execAfterParsing(result: MoveHistResult?) {
            if (result != null) {
                debugProcess.historyChanged(result.topHist, result.botHist,
                        object : HsStackFrame(debugProcess, result.filePosition, result.bindingList.list) {
                            override fun tryGetBindings() {
                            }

                        })
            }
        }

    }
}