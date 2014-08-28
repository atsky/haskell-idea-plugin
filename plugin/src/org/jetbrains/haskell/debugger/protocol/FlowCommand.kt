package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.GHCiParser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import org.jetbrains.haskell.debugger.frames.HsDebuggerEvaluator
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XValue
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.parser.JSONConverter
import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointDescription

/**
 * Base class for commands that continue program execution until reaching breakpoint or finish
 * (trace and continue commands)
 *
 * Created by vlad on 7/17/14.
 */

public abstract class FlowCommand(callback: CommandCallback<HsStackFrameInfo?>?)
: AbstractCommand<HsStackFrameInfo?>(callback) {

    override fun parseGHCiOutput(output: Deque<String?>): HsStackFrameInfo? = GHCiParser.tryParseStoppedAt(output)

    override fun parseJSONOutput(output: JSONObject): HsStackFrameInfo? =
            JSONConverter.stoppedAtFromJSON(output)

    class object {

        public class StandardFlowCallback(val debugger: ProcessDebugger,
                                          val debugRespondent: DebugRespondent) : CommandCallback<HsStackFrameInfo?>() {

            override fun execBeforeSending() = debugRespondent.resetHistoryStack()

            override fun execAfterParsing(result: HsStackFrameInfo?) {
                if (result != null) {
                    if (result.filePosition == null) {
                        setExceptionContext(result)
                        return
                    }
                    val module = debugRespondent.getModuleByFile(result.filePosition.filePath)
                    val breakpoint = debugRespondent.getBreakpointAt(module, result.filePosition.rawStartLine)
                    val condition = breakpoint?.condition
                    if (breakpoint != null && condition != null) {
                        handleCondition(breakpoint, condition, result)
                    } else {
                        setContext(result, breakpoint)
                    }
                } else {
                    debugRespondent.traceFinished()
                }
            }

            private fun handleCondition(breakpoint: HaskellLineBreakpointDescription, condition: String, result: HsStackFrameInfo) {
                val evaluator = HsDebuggerEvaluator(debugger)
                evaluator.evaluate(condition, object : XDebuggerEvaluator.XEvaluationCallback {
                    override fun errorOccurred(errorMessage: String) {
                        val msg = "Condition \"$condition\" of breakpoint at line ${breakpoint.line}" +
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
                            debugger.resume()
                        }
                    }

                }, null)
            }

            private fun setExceptionContext(result: HsStackFrameInfo) {
                val frame = HsHistoryFrame(debugger, result)
                frame.obsolete = false
                debugRespondent.historyChange(frame, null)
                val context = HsSuspendContext(debugger, ProgramThreadInfo(null, "Main", result))
                debugRespondent.exceptionReached(context)
            }

            private fun setContext(result: HsStackFrameInfo, breakpoint: HaskellLineBreakpointDescription?) {
                val frame = HsHistoryFrame(debugger, result)
                frame.obsolete = false
                debugger.history(HistoryCommand.DefaultHistoryCallback(debugger, debugRespondent, frame, breakpoint))
            }
        }
    }
}
