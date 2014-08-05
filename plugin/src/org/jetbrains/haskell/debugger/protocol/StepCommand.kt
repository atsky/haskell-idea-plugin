package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.parser.ShowOutput
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.frames.HsTopStackFrame

/**
 * Created by vlad on 7/17/14.
 */

public abstract class StepCommand(callback: CommandCallback<HsStackFrameInfo?>?)
: AbstractCommand<HsStackFrameInfo?>(callback) {

    override fun parseGHCiOutput(output: Deque<String?>): HsStackFrameInfo? {
        return Parser.tryParseStoppedAt(output)
    }

    class object {
        public class StandardStepCallback(val debugProcess: HaskellDebugProcess) : CommandCallback<HsStackFrameInfo?>() {
            override fun execAfterParsing(result: HsStackFrameInfo?) {
                if (result != null && result is HsStackFrameInfo) {
                    val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", result))
                    debugProcess.historyChanged(true, false, HsTopStackFrame(debugProcess, result))
                    debugProcess.getSession()!!.positionReached(context)
                }
            }
        }
    }
}
