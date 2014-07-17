package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.GHCiDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import java.util.ArrayList
import org.jetbrains.haskell.debugger.HaskellStackFrameInfo
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.HaskellSuspendContext
import org.jetbrains.haskell.debugger.ProgramThreadInfo

/**
 * Created by vlad on 7/16/14.
 */

public class HistoryCommand(val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?) : RealTimeCommand() {

    override fun getBytes(): ByteArray {
        return ":hist\n".toByteArray()
    }

    override fun handleOutput(output: Deque<String?>, debugProcess: GHCiDebugProcess) {
        val history = Parser.parseHistory(output)
        val frames = ArrayList<HaskellStackFrameInfo>()
        for (callInfo in history.list) {
            frames.add(HaskellStackFrameInfo(callInfo.position))
        }
        val context = HaskellSuspendContext(ProgramThreadInfo(null, "Main", frames))
        if (breakpoint != null) {
            debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
        } else {
            debugProcess.getSession()!!.positionReached(context)
        }
    }
}
