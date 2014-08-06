package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.JSONResult
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import org.jetbrains.haskell.debugger.utils.SyncObject

/**
 * @author Habibullin Marat
 */
//public class BreakListForLineCommand(private val module: String,
//                                     private val lineNumber: Int,
//                                     callback: SyncCommandCallback<JSONResult>) : SyncCommand<JSONResult>(callback) {
//    override fun getText(): String = ":breaklist $module $lineNumber\n"
//    override fun parseGHCiOutput(output: Deque<String?>): JSONResult = Parser.parseJSONObject(output)
//
//    class object {
//        public class DefaultCallback(syncObject: SyncObject, private val resultList: ArrayList<HsFilePosition>)
//        : SyncCommandCallback<JSONResultList>(syncObject) {
//            override fun execAfterParsing(result: JSONResultList) {
//                for(jsonObj in result.jsonList) {
//                }
//            }
//        }
//    }
//}