package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.HistoryResult
import java.util.Deque
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.GHCiParser
import org.jetbrains.haskell.debugger.parser.JSONConverter
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.procdebuggers.ProcessDebugger
import org.jetbrains.haskell.debugger.procdebuggers.utils.DebugRespondent
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointDescription

/**
 * Created by vlad on 8/7/14.
 */
public class HistoryCommand(callback: CommandCallback<HistoryResult?>) : RealTimeCommand<HistoryResult?>(callback) {

    override fun getText(): String {
        return ":history\n"
    }
    override fun parseGHCiOutput(output: Deque<String?>): HistoryResult? = GHCiParser.parseHistoryResult(output)

    override fun parseJSONOutput(output: JSONObject): HistoryResult? = JSONConverter.historyResultFromJSON(output)

    companion object {
        public class DefaultHistoryCallback(val debugger: ProcessDebugger,
                                            val debugRespondent: DebugRespondent,
                                            val historyFrame: HsHistoryFrame,
                                            val breakpoint: HaskellLineBreakpointDescription?) : CommandCallback<HistoryResult?>() {

            override fun execAfterParsing(result: HistoryResult?) {
                debugRespondent.historyChange(historyFrame, result)
                val context = HsSuspendContext(debugger, ProgramThreadInfo(null, "Main", historyFrame.stackFrameInfo))
                if (breakpoint != null) {
                    debugRespondent.breakpointReached(breakpoint, context)
                } else {
                    debugRespondent.positionReached(context)
                }
            }
        }
    }
}