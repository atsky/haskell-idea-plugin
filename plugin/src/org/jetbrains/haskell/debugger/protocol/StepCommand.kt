package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.GHCiParser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import org.jetbrains.haskell.debugger.parser.ShowOutput
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.frames.HsTopStackFrame
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.parser.JSONConverter

/**
 * Created by vlad on 7/17/14.
 */

public abstract class StepCommand(callback: CommandCallback<HsStackFrameInfo?>?)
: AbstractCommand<HsStackFrameInfo?>(callback) {

    override fun parseGHCiOutput(output: Deque<String?>): HsStackFrameInfo? = GHCiParser.tryParseStoppedAt(output)

    override fun parseJSONOutput(output: JSONObject): HsStackFrameInfo? =
            JSONConverter.stoppedAtFromJSON(output)

    class object {
        public class StandardStepCallback(val debugProcess: HaskellDebugProcess) : CommandCallback<HsStackFrameInfo?>() {

            override fun execBeforeSending() {
                debugProcess.resetHistory()
                debugProcess.historyChanged(false, false, null)
            }

            override fun execAfterParsing(result: HsStackFrameInfo?) {
                if (result != null && result is HsStackFrameInfo) {
                    val frame = HsHistoryFrame(debugProcess, result)
                    frame.obsolete = false
                    val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", result))
                    debugProcess.historyFrameAppeared(frame)
                    debugProcess.historyChanged(false, true, frame)
                    debugProcess.getSession()!!.positionReached(context)
                }
            }
        }
    }
}
