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
import org.jetbrains.haskell.debugger.parser.HsGeneralStackFrameInfo

/**
 * Created by vlad on 7/16/14.
 */

public class HistoryCommand(val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?,
                            val topFrameInfo : HsTopStackFrameInfo) : RealTimeCommand() {
    override fun getBytes(): ByteArray {
        return ":hist\n".toByteArray()
    }

    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        val histFrames = ArrayList<HsGeneralStackFrameInfo>()
        histFrames.addAll(Parser.parseHistory(output))
        val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", topFrameInfo, histFrames))
        if (breakpoint != null) {
            debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
        } else {
            debugProcess.getSession()!!.positionReached(context)
        }
    }
}
