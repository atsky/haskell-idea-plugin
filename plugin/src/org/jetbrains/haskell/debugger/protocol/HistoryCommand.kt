package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.HistoryResult
import java.util.Deque
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.GHCiParser
import org.jetbrains.haskell.debugger.parser.JSONConverter

/**
 * Created by vlad on 8/7/14.
 */
public class HistoryCommand(callback: CommandCallback<HistoryResult?>) : RealTimeCommand<HistoryResult?>(callback) {

    override fun getText(): String {
        return ":history"
    }
    override fun parseGHCiOutput(output: Deque<String?>): HistoryResult? = GHCiParser.parseHistoryResult(output)

    override fun parseJSONOutput(output: JSONObject): HistoryResult? = JSONConverter.historyResultFromJSON(output)

}