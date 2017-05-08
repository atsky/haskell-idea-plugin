package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult
import java.util.Deque
import org.json.simple.JSONObject
import org.apache.commons.lang.NotImplementedException
import org.jetbrains.haskell.debugger.parser.BreakInfoList
import org.jetbrains.haskell.debugger.utils.SyncObject
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import org.jetbrains.haskell.debugger.parser.JSONConverter
import org.jetbrains.haskell.debugger.parser.BreakInfo

/**
 * Created by vlad on 7/31/14.
 */

class BreakpointListCommand(val module: String,
                                   val lineNumber: Int? = null,
                                   syncObj: SyncObject,
                                   callback: CommandCallback<BreakInfoList?>)
: SyncCommand<BreakInfoList?>(syncObj, callback) {

    override fun getText(): String {
        if(lineNumber == null) {
            return ":breaklist $module\n"
        }
        return ":breaklist $module $lineNumber\n"
    }

    override fun parseGHCiOutput(output: Deque<String?>): BreakInfoList? {
        throw RuntimeException("BreakpointListCommand.parseGHCiOutput: not supported in ghci")
    }

    override fun parseJSONOutput(output: JSONObject): BreakInfoList? = JSONConverter.breaksListFromJSON(output)

    companion object {
        class DefaultCallback(private val resultList: ArrayList<BreakInfo>)
        : CommandCallback<BreakInfoList?>() {
            override fun execAfterParsing(result: BreakInfoList?) {
                if(result != null) {
                    for (filePos in result.list) {
                        resultList.add(filePos)
                    }
                }
            }
        }
    }
}