package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import java.util.ArrayList
import org.jetbrains.haskell.debugger.frames.HaskellStackFrameInfo
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.frames.HaskellSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo

/**
 * Created by vlad on 7/16/14.
 */

public class HistoryCommand(val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?,
                            val topFrameInfo : HaskellStackFrameInfo) : SuspendContextSetterCommand() {
    override fun getBytes(): ByteArray {
        return ":hist\n".toByteArray()
    }

    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        val singleFrameList = ArrayList<HaskellStackFrameInfo>()
        singleFrameList.add(topFrameInfo)
        val histEntriesNumber = Parser.parseHistory(output)
        if(histEntriesNumber <= 0) {
            setSuspendContext(breakpoint, singleFrameList, debugProcess)
        } else {
            debugProcess.debugger.back(SequenceOfBacksCommand(breakpoint, singleFrameList, histEntriesNumber - 1))
        }
    }
}
