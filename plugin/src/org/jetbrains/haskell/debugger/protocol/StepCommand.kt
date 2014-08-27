package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.GHCiParser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.parser.JSONConverter
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger
import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent

/**
 * Created by vlad on 7/17/14.
 */

public abstract class StepCommand(callback: CommandCallback<HsStackFrameInfo?>?)
: AbstractCommand<HsStackFrameInfo?>(callback) {

    override fun parseGHCiOutput(output: Deque<String?>): HsStackFrameInfo? = GHCiParser.tryParseStoppedAt(output)

    override fun parseJSONOutput(output: JSONObject): HsStackFrameInfo? =
            JSONConverter.stoppedAtFromJSON(output)

    class object {
        public class StandardStepCallback(val debugger: ProcessDebugger,
                                          val debugRespondent: DebugRespondent) : CommandCallback<HsStackFrameInfo?>() {

            override fun execBeforeSending() = debugRespondent.resetHistoryStack()

            override fun execAfterParsing(result: HsStackFrameInfo?) {
                if (result != null) {
                    val frame = HsHistoryFrame(debugger, result)
                    frame.obsolete = false
                    debugger.history(HistoryCommand.DefaultHistoryCallback(debugger, debugRespondent, frame, null))
                } else {
                    debugRespondent.traceFinished()
                }
            }
        }
    }
}
