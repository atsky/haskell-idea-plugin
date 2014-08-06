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
import org.json.simple.JSONObject

/**
 * Base class for commands that continue program execution until reaching breakpoint or finish
 * (trace and continue commands)
 *
 * Created by vlad on 7/17/14.
 */

public abstract class FlowCommand(callback: CommandCallback<HsStackFrameInfo?>?)
: AbstractCommand<HsStackFrameInfo?>(callback) {

    override fun parseGHCiOutput(output: Deque<String?>): HsStackFrameInfo? = Parser.tryParseStoppedAt(output)

    override fun parseJSONOutput(output: JSONObject): HsStackFrameInfo? =
            Parser.stoppedAtFromJSON(output)
}
