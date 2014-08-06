package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.protocol.AbstractCommand
import org.jetbrains.haskell.debugger.protocol.TraceCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.RemoveBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.StepIntoCommand
import org.jetbrains.haskell.debugger.protocol.StepOverCommand
import org.jetbrains.haskell.debugger.protocol.ResumeCommand
import org.jetbrains.haskell.debugger.protocol.HiddenCommand
import org.jetbrains.haskell.debugger.protocol.RealTimeCommand
import com.intellij.openapi.util.Key
import com.intellij.execution.process.ProcessOutputTypes
import java.util.concurrent.atomic.AtomicBoolean
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
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
import org.jetbrains.haskell.debugger.parser.MoveHistResult
import org.jetbrains.haskell.debugger.protocol.PrintCommand
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.frames.HsDebuggerEvaluator
import com.intellij.xdebugger.frame.XValue
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.protocol.ForwardCommand

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
            queue.addCommand(TraceCommand(TRACE_CMD, StandardFlowCallback()))

    /**
     * Executes command immediately
     */
    private fun execute(command: AbstractCommand<out ParseResult?>) {
        val text = command.getText()

        synchronized(writeLock) {
            lastCommand = command

            if (lastCommand !is HiddenCommand) {
                debugProcess.printToConsole(text, ConsoleViewContentType.SYSTEM_OUTPUT)
            }

            val os = debugProcess.getProcessHandler().getProcessInput()!!
            os.write(text.toByteArray())
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
            queue.addCommand(StepIntoCommand(StandardStepCallback()))

    override fun stepOver() =
            queue.addCommand(StepOverCommand(StandardStepCallback()))

    override fun runToPosition(module: String, line: Int) {
        if (debugProcess.getBreakpointAtPosition(module, line) == null) {
            queue.addCommand(SetBreakpointCommand(module, line, SetTempBreakCallback()))
        } else {
            if (debugStarted) resume() else trace()
        }
    }

    override fun resume() =
            queue.addCommand(ResumeCommand(StandardFlowCallback()))

    override fun back(backCommand: BackCommand) =
            queue.addCommand(backCommand)

    override fun forward() =
            queue.addCommand(ForwardCommand(null))

    override fun print(printCommand: PrintCommand) = queue.addCommand(printCommand)

    override fun force(forceCommand: ForceCommand) = queue.addCommand(forceCommand)

    override fun updateBinding(binding: LocalBinding, lock: Lock, condition: Condition) {
        lock.lock()
        queue.addCommand(ExpressionTypeCommand(binding.name!!, object : CommandCallback<ExpressionType?>() {
            override fun execAfterParsing(result: ExpressionType?) {
                lock.lock()
                binding.typeName = result?.expressionType
                queue.addCommand(PrintCommand(binding.name!!, object : CommandCallback<LocalBinding?>() {
                    override fun execAfterParsing(result: LocalBinding?) {
                        lock.lock()
                        binding.value = result?.value
                        condition.signalAll()
                        lock.unlock()
                    }
                }))
                lock.unlock()
            }
        }))
        lock.unlock()
    }

    override fun sequenceCommand(command: AbstractCommand<*>, length: Int) {
        for (i in 0..length) {
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

    private inner class RunToPositionCallback(val breakpointNumber: Int) : CommandCallback<HsStackFrameInfo?>() {
        override fun execAfterParsing(result: HsStackFrameInfo?) {
            if (result == null) {
                throw RuntimeException("Wrong result obtained while running to the temporary breakpoint")
            }
            queue.addCommand(RemoveBreakpointCommand(null, breakpointNumber, RemoveTempBreakCallback(result)), true)
        }
    }

    private inner class RemoveTempBreakCallback(val flowResult: HsStackFrameInfo)
    : CommandCallback<ParseResult?>() {
        override fun execAfterParsing(result: ParseResult?) = StandardFlowCallback().execAfterParsing(flowResult)
    }

    private inner class StandardFlowCallback() : CommandCallback<HsStackFrameInfo?>() {

        override fun execBeforeSending() {
            debugProcess.resetHistory()
            debugProcess.historyChanged(false, false, null)
        }

        override fun execAfterParsing(result: HsStackFrameInfo?) {
            if (result != null) {
                val moduleName = HaskellUtils.getModuleName(debugProcess.getSession()!!.getProject(),
                        LocalFileSystem.getInstance()!!.findFileByPath(result.filePosition.filePath)!!)
                val breakpoint = debugProcess.getBreakpointAtPosition(moduleName, result.filePosition.rawStartLine)
                val condition = breakpoint?.getCondition()
                if (breakpoint != null && condition != null) {
                    handleCondition(breakpoint, condition, result)
                } else if (breakpoint != null) {
                    setContext(result, breakpoint)
                } else {
                    Notifications.Bus.notify(Notification("", "Wrong breakpoint condition", "No breakpoint in line", NotificationType.WARNING))
                    debugProcess.getSession()!!.stop()
                }
            }
        }

        private fun handleCondition(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, condition: String, result: HsStackFrameInfo) {
            val evaluator = HsDebuggerEvaluator(debugProcess.debugger)
            evaluator.evaluate(condition, object : XDebuggerEvaluator.XEvaluationCallback {
                override fun errorOccurred(errorMessage: String) {
                    val msg = "Condition \"$condition\" of breakpoint at line ${breakpoint.getLine()}" +
                            "cannot be evaluated, reason: $errorMessage"
                    Notifications.Bus.notify(Notification("", "Wrong breakpoint condition", msg, NotificationType.WARNING))
                    setContext(result, breakpoint)
                }
                override fun evaluated(evalResult: XValue) {
                    if (evalResult is HsDebugValue &&
                            evalResult.binding.typeName == HaskellUtils.HS_BOOLEAN_TYPENAME &&
                            (evalResult as HsDebugValue).binding.value == HaskellUtils.HS_BOOLEAN_TRUE) {
                        setContext(result, breakpoint)
                    } else {
                        debugProcess.debugger.resume()
                    }
                }

            }, null)
        }

        private fun setContext(result: HsStackFrameInfo, breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
            val frame = HsHistoryFrame(debugProcess, result)
            frame.obsolete = false
            val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", result))
            debugProcess.historyFrameAppeared(frame)
            debugProcess.historyChanged(false, true, frame)
            debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
        }
    }

    private inner class StandardStepCallback() : CommandCallback<HsStackFrameInfo?>() {

        override fun execBeforeSending() {
            debugProcess.resetHistory()
            debugProcess.historyChanged(false, false, null)
        }

        override fun execAfterParsing(result: HsStackFrameInfo?) {
            if (result != null && result is HsStackFrameInfo) {
                val frame = HsHistoryFrame(debugProcess, result)
                frame.obsolete = false
                val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", result))
                debugProcess.historyFrameAppeared(frame)
                debugProcess.historyChanged(false, true, frame)
                debugProcess.getSession()!!.positionReached(context)
            }
        }
    }
}