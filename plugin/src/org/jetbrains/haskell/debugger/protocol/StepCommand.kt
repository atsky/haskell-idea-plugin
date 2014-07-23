package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo

/**
 * Created by vlad on 7/17/14.
 */

public abstract class StepCommand(callback: CommandCallback?) : AbstractCommand(callback) {

    override fun parseOutput(output: Deque<String?>): ParseResult? {
        return Parser.tryParseStoppedAt(output)
    }

    class object {
        public class StandardStepCallback(val debugProcess: HaskellDebugProcess) : CommandCallback() {
            override fun execAfterHandling(result: ParseResult?) {
                if (result != null && result is HsTopStackFrameInfo) {
                    debugProcess.debugger.history(null, result)
                }
            }
        }
    }
}
