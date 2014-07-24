package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import java.util.ArrayList
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.parser.HsCommonStackFrameInfo
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.History

/**
 * Created by vlad on 7/16/14.
 */

public class HistoryCommand(
                            callback: CommandCallback?) : RealTimeCommand(callback) {
    override fun getBytes(): ByteArray {
        return ":hist\n".toByteArray()
    }

    override fun parseOutput(output: Deque<String?>): ParseResult? =Parser.parseHistory(output)

    class object {
        public class StandardHistoryCallback(val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?,
                                          val topFrameInfo: HsTopStackFrameInfo,
                                          val debugProcess: HaskellDebugProcess) : CommandCallback() {
            override fun execAfterParsing(result: ParseResult?) {
                if (result != null && result is History) {
                    val histFrames = result.list
                    val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", topFrameInfo, histFrames))
                    if (breakpoint != null) {
                        debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
                    } else {
                        debugProcess.getSession()!!.positionReached(context)
                    }
                }
            }
        }
    }
}
