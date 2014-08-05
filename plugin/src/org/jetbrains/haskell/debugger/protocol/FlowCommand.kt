package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import org.jetbrains.haskell.debugger.frames.HsDebuggerEvaluator
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XValue
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.frames.HsTopStackFrame

/**
 * Base class for commands that continue program execution until reaching breakpoint or finish
 * (trace and continue commands)
 *
 * Created by vlad on 7/17/14.
 */

public abstract class FlowCommand(callback: CommandCallback<HsStackFrameInfo?>?)
: AbstractCommand<HsStackFrameInfo?>(callback) {

    override fun parseGHCiOutput(output: Deque<String?>): HsStackFrameInfo? = Parser.tryParseStoppedAt(output)

    class object {
        public class StandardFlowCallback(val debugProcess: HaskellDebugProcess)
        : CommandCallback<HsStackFrameInfo?>() {
            override fun execAfterParsing(result: HsStackFrameInfo?) {
                if (result != null) {
                    val moduleName = HaskellUtils.getModuleName(debugProcess.getSession()!!.getProject(),
                            LocalFileSystem.getInstance()!!.findFileByPath(result.filePosition.filePath)!!)
                    val breakpoint = debugProcess.getBreakpointAtPosition(moduleName, result.filePosition.rawStartLine)
                    val condition = breakpoint?.getCondition()
                    if (breakpoint != null && condition != null) {
                        handleCondition(breakpoint, condition, result)
                    } else {
                        val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", result))
                        debugProcess.historyChanged(true, false, HsTopStackFrame(debugProcess, result))
                        debugProcess.getSession()!!.breakpointReached(breakpoint!!, breakpoint.getLogExpression(), context)
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
                        val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", result))
                        debugProcess.historyChanged(true, false, HsTopStackFrame(debugProcess, result))
                        debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
                    }
                    override fun evaluated(evalResult: XValue) {
                        if (evalResult is HsDebugValue &&
                                evalResult.binding.typeName == HaskellUtils.HS_BOOLEAN_TYPENAME &&
                                (evalResult as HsDebugValue).binding.value == HaskellUtils.HS_BOOLEAN_TRUE) {
                            val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", result))
                            debugProcess.historyChanged(true, false, HsTopStackFrame(debugProcess, result))
                            debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
                        } else {
                            debugProcess.debugger.resume()
                        }
                    }

                }, null)
            }
        }
    }
}
