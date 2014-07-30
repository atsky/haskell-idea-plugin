package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.parser.ShowOutput

/**
 * Created by vlad on 7/17/14.
 */

public abstract class StepCommand(callback: CommandCallback<HsTopStackFrameInfo?>?)
: AbstractCommand<HsTopStackFrameInfo?>(callback) {

    override fun parseGHCiOutput(output: Deque<String?>): HsTopStackFrameInfo? {
        return Parser.tryParseStoppedAt(output)
    }

    class object {
        public class StandardStepCallback(val debugProcess: HaskellDebugProcess) : CommandCallback<HsTopStackFrameInfo?>() {
            override fun execAfterParsing(result: HsTopStackFrameInfo?) {
                if (result != null && result is HsTopStackFrameInfo) {
                    debugProcess.debugger.history(null, result)
                }
            }
        }
    }
}
