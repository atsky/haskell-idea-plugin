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

public class HistoryCommand(callback: CommandCallback<History?>?) : RealTimeCommand<History?>(callback) {
    override fun getBytes(): ByteArray {
        return ":history\n".toByteArray()
    }

    override fun parseGHCiOutput(output: Deque<String?>): History? = Parser.parseHistory(output)
}
