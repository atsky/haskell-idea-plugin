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
import org.jetbrains.haskell.debugger.parser.Parser
import org.json.simple.JSONObject
import com.intellij.execution.ui.ConsoleViewContentType
import org.jetbrains.haskell.debugger.protocol.BreakpointListCommand
import org.jetbrains.haskell.debugger.protocol.SetBreakpointCommand
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.ArrayList
import org.json.simple.JSONArray
import org.jetbrains.haskell.debugger.protocol.HistoryCommand
import org.jetbrains.haskell.debugger.parser.History
import org.jetbrains.haskell.debugger.parser.HsCommonStackFrameInfo
import org.jetbrains.haskell.debugger.protocol.FlowCommand
import org.jetbrains.haskell.debugger.protocol.ResumeCommand
import org.jetbrains.haskell.debugger.protocol.StepIntoCommand
import org.jetbrains.haskell.debugger.protocol.StepOverCommand
import org.jetbrains.haskell.debugger.protocol.RemoveBreakpointCommand
import org.jetbrains.haskell.debugger.protocol.CommandCallback
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult

/**
 * Created by vlad on 7/30/14.
 */

public class RemoteDebugger(val debugProcess: HaskellDebugProcess) : ProcessDebugger {

    private val queue: CommandQueue
    private val handler: JSONHandler = JSONHandler()
    private val writeLock = Any()

    private var lastCommand: AbstractCommand<out ParseResult?>? = null;

    {
        queue = CommandQueue({(command: AbstractCommand<out ParseResult?>) -> execute(command) })
        queue.start()
    }

    public var debugStarted: Boolean = false
        private set

    private fun execute(command: AbstractCommand<out ParseResult?>) {
        val bytes = command.getBytes()

        synchronized(writeLock) {
            lastCommand = command

            command.callback?.execBeforeSending()

            if (command !is HiddenCommand) {
                debugProcess.printToConsole(String(bytes), ConsoleViewContentType.SYSTEM_OUTPUT)
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

    override fun trace() =
            queue.addCommand(TraceCommand("main", null))

    override fun setBreakpoint(module: String, line: Int) =
            queue.addCommand(SetBreakpointCommand(module, line, null))

    override fun removeBreakpoint(module: String, breakpointNumber: Int) =
            queue.addCommand(RemoveBreakpointCommand(module, breakpointNumber, null))

    override fun close() = queue.stop()

    override fun stepInto() = queue.addCommand(StepIntoCommand(null))

    override fun stepOver() = queue.addCommand(StepOverCommand(null))

    override fun runToPosition(module: String, line: Int) =
            queue.addCommand(SetBreakpointCommand(module, line,
                    object : CommandCallback<BreakpointCommandResult?>() {
                        override fun execAfterParsing(result: BreakpointCommandResult?) {
                        }
                        override fun execBeforeSending() {
                            handler.inRunToPosition = true
                        }
                    }))

    override fun resume() = queue.addCommand(ResumeCommand(null))

    override fun prepareDebugger() {
    }

    override fun history(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>?, topFrameInfo: HsTopStackFrameInfo) {
        handler.breakpoint = breakpoint
        handler.topFrameInfo = topFrameInfo
        queue.addCommand(HistoryCommand(null))
    }

    override fun backsSequence(sequenceOfBacksCommand: SequenceOfBacksCommand) =
            queue.addCommand(sequenceOfBacksCommand)

    override fun forwardsSequence(sequenceOfForwardsCommand: SequenceOfForwardsCommand) =
            queue.addCommand(sequenceOfForwardsCommand)

    override fun onTextAvailable(text: String, outputType: Key<out Any?>?) {
        handler.handle(Parser.parseJSONObject(text).json)
        queue.setReadyForInput()
    }

    private fun breakpointList(module: String, lineToSet: Int? = null) =
            queue.addCommand(BreakpointListCommand(module, lineToSet, null))

    public inner class JSONHandler {
        private val CONNECTED_MSG = "connected to port"

        private val WARNING_MSG = "warning"
        private val EXCEPTION_MSG = "exception"

        private val PAUSED_MSG = "paused"
        private val FINISHED_MSG = "finished"

        private val BREAKPOINT_SET_MSG = "breakpoint was set"
        private val BREAKPOINT_NOT_SET_MSG = "breakpoint was not set"

        private val BREAKPOINT_REMOVED_MSG = "breakpoint was removed"
        private val BREAKPOINT_NOT_REMOVED_MSG = "breakpoint was not removed"

        private val HISTORY_MSG = "got history"

        // For history command
        public var breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>? = null
        public var topFrameInfo: HsTopStackFrameInfo? = null

        // For runToPosition command combination
        public var inRunToPosition: Boolean = false
        public var tempBreakpointIndex: Int? = null

        public fun handle(result: JSONObject) {
            val info = result.get("info") as String?
            when (info) {
                null ->
                    throw RuntimeException("Missing data type")
                CONNECTED_MSG ->
                    debugProcess.printToConsole("Connected to port: ${result.get("port")}\n",
                            ConsoleViewContentType.SYSTEM_OUTPUT)
                WARNING_MSG ->
                    debugProcess.printToConsole("WARNING: ${result.getString("message")}\n",
                            ConsoleViewContentType.ERROR_OUTPUT)
                EXCEPTION_MSG ->
                    debugProcess.printToConsole("EXCEPTION: ${result.getString("message")}\n",
                            ConsoleViewContentType.ERROR_OUTPUT)
                PAUSED_MSG ->
                    paused(result)
                FINISHED_MSG ->
                    debugProcess.getSession()!!.stop()
                BREAKPOINT_SET_MSG -> {
                    if (!inRunToPosition) {
                        debugProcess.setBreakpointNumberAtLine(result.getInt("index"),
                                (lastCommand as SetBreakpointCommand).module, result.getObject("src_span").getInt("startline"))
                    } else {
                        queue.addCommand(ResumeCommand(null), true)
                        queue.addCommand(RemoveBreakpointCommand((lastCommand as SetBreakpointCommand).module, result.getInt("index"), null), true)
                    }
                }
                BREAKPOINT_NOT_SET_MSG ->
                    debugProcess.printToConsole("Breakpoint was not set: ${result.getString("add_info")}\n",
                            ConsoleViewContentType.SYSTEM_OUTPUT)
                BREAKPOINT_REMOVED_MSG -> {
                    if (inRunToPosition) {
                        inRunToPosition = false
                    }
                }
                BREAKPOINT_NOT_REMOVED_MSG ->
                    debugProcess.printToConsole("Breakpoint was not removed: ${result.getString("add_info")}\n",
                            ConsoleViewContentType.SYSTEM_OUTPUT)
                HISTORY_MSG ->
                    gotHistory(result)
                else ->
                    throw RuntimeException("Unknown result")
            }
        }

        private fun paused(result_json: JSONObject) {
            val srcSpan = result_json.getObject("src_span")
            val result = HsTopStackFrameInfo(getFilePosition(srcSpan),
                    ArrayList(result_json.getArray("names").toArray().map {(name) -> LocalBinding(name as String, null, null) }))
            FlowCommand.StandardFlowCallback(debugProcess).execAfterParsing(result)
        }

        private fun gotHistory(result_json: JSONObject) {
            val result = History(ArrayList(result_json.getArray("history").map {
                (line) ->
                with(line as JSONObject) {
                    HsCommonStackFrameInfo(getInt("index"), getString("function"), getFilePosition(getObject("src_span")), null)
                }
            }))
            HistoryCommand.StandardHistoryCallback(breakpoint, topFrameInfo!!, debugProcess).execAfterParsing(result)
        }

        private fun getFilePosition(srcSpan: JSONObject): HsFilePosition =
                HsFilePosition(srcSpan.getString("file"), srcSpan.getInt("startline"), srcSpan.getInt("startcol"),
                        srcSpan.getInt("endline"), srcSpan.getInt("endcol"))

        private fun JSONObject.getInt(key: String): Int {
            return (get(key) as Long).toInt()
        }

        private fun JSONObject.getString(key: String): String {
            return get(key) as String
        }

        private fun JSONObject.getObject(key: String): JSONObject {
            return get(key) as JSONObject
        }

        private fun JSONObject.getArray(key: String): JSONArray {
            return get(key) as JSONArray
        }
    }

}