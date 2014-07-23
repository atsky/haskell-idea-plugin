package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.frame.XSuspendContext
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.GHCiDebugger
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo

/**
 * Created by vlad on 7/17/14.
 */

public abstract class FlowCommand(callback: CommandCallback?) : AbstractCommand(callback) {

    override fun parseOutput(output: Deque<String?>): ParseResult? = Parser.tryParseStoppedAt(output)

    class object {
        public class StandardFlowCallback(val debugProcess: HaskellDebugProcess): CommandCallback() {
            override fun execAfterHandling(result: ParseResult?) {
                if (result != null && result is HsTopStackFrameInfo) {
                    val breakpoint = debugProcess.getBreakpointAtLine(result.filePosition.startLine)!!
                    debugProcess.debugger.history(breakpoint, result)
                }
            }
        }
    }
}
