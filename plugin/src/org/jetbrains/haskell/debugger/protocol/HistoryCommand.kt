package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.HistoryResult
import java.util.Deque
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.GHCiParser
import org.jetbrains.haskell.debugger.parser.JSONConverter
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.parser.HsHistoryFrameInfo

/**
 * Created by vlad on 8/7/14.
 */
public class HistoryCommand(callback: CommandCallback<HistoryResult?>) : RealTimeCommand<HistoryResult?>(callback) {

    override fun getText(): String {
        return ":history\n"
    }
    override fun parseGHCiOutput(output: Deque<String?>): HistoryResult? = GHCiParser.parseHistoryResult(output)

    override fun parseJSONOutput(output: JSONObject): HistoryResult? = JSONConverter.historyResultFromJSON(output)

    class object {
        public class DefaultHistoryCallback(val debugProcess: HaskellDebugProcess,
                                            val historyFrame: HsHistoryFrame,
                                            val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?) : CommandCallback<HistoryResult?>() {

            override fun execAfterParsing(result: HistoryResult?) {
                val context = HsSuspendContext(debugProcess, ProgramThreadInfo(null, "Main", historyFrame.stackFrameInfo))
                debugProcess.historyManager.historyFrameAppeared(historyFrame)
                if (result != null) {
                    debugProcess.historyManager.
                            setHistoryFramesInfo(HsHistoryFrameInfo(0, null, historyFrame.stackFrameInfo.filePosition), result.frames, result.full)
                }
                debugProcess.historyManager.historyChanged(false, true, historyFrame)
                if (breakpoint != null) {
                    debugProcess.getSession()!!.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
                } else {
                    debugProcess.getSession()!!.positionReached(context)
                }
            }
        }
    }
}