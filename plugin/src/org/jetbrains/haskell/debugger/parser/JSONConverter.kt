package org.jetbrains.haskell.debugger.parser

import org.json.simple.JSONObject
import org.json.simple.JSONArray
import org.json.simple.parser.JSONParser
import java.util.ArrayList

/**
 * Converts Json objects to appropriate types to use then in commands
 *
 * @author Habibullin Marat
 */
public class JSONConverter {
    class object {
        private val CONNECTED_MSG = "connected to port"

        private val WARNING_MSG = "warning"
        private val EXCEPTION_MSG = "exception"

        private val PAUSED_MSG = "paused"
        private val FINISHED_MSG = "finished"

        private val BREAKPOINT_SET_MSG = "breakpoint was set"
        private val BREAKPOINT_NOT_SET_MSG = "breakpoint was not set"

        private val BREAKPOINT_REMOVED_MSG = "breakpoint was removed"
        private val BREAKPOINT_NOT_REMOVED_MSG = "breakpoint was not removed"

        private val BACK_MSG = "stepped back"
        private val FORWARD_MSG = "stepped forward"

        private val EXPRESSION_TYPE_MSG = "expression type"

        private val EVALUATED_MSG = "evaluated"

        private val BREAK_LIST_FOR_LINE_INFO = "break list for line"
        private val BREAKS_TAG = "breaks"
        private val BREAK_INDEX_TAG = "index"
        private val SRC_SPAN_TAG = "src_span"

        public fun checkExceptionFromJSON(json: JSONObject): ExceptionResult? {
            if (WARNING_MSG.equals(json.get("info")) || EXCEPTION_MSG.equals(json.get("info"))) {
                return ExceptionResult(json.getString("message"))
            } else {
                return null
            }
        }

        public fun breakpointCommandResultFromJSON(json: JSONObject): BreakpointCommandResult? {
            val info = json.getString("info")
            if (info.equals(BREAKPOINT_NOT_SET_MSG)) {
                return null
            } else if (info.equals(BREAKPOINT_SET_MSG)) {
                return BreakpointCommandResult(json.getInt("index"), filePositionFromJSON(json.getObject("src_span")))
            } else {
                throw RuntimeException("Wrong json output occured while handling SetBreakpointCommand result")
            }
        }

        public fun filePositionFromJSON(json: JSONObject): HsFilePosition {
            return HsFilePosition(json.getString("file"), json.getInt("startline"), json.getInt("startcol"),
                    json.getInt("endline"), json.getInt("endcol"))
        }

        public fun stoppedAtFromJSON(json: JSONObject): HsStackFrameInfo? {
            val info = json.getString("info")
            if (info.equals(FINISHED_MSG)) {
                return null
            } else if (info.equals(PAUSED_MSG)) {
                return HsStackFrameInfo(filePositionFromJSON(json.getObject("src_span")),
                        localBindingListFromJSONArray(json.getArray("vars")).list)
            } else {
                throw RuntimeException("Wrong json output occured while handling flow command result")
            }
        }

        public fun localBindingListFromJSONArray(json: JSONArray): LocalBindingList =
                LocalBindingList(ArrayList(json.toArray().map {
                    (variable) ->
                    with (variable as JSONObject) {
                        LocalBinding(getString("name"), get("type") as String?, get("value") as String?)
                    }
                }))

        public fun moveHistResultFromJSON(json: JSONObject): MoveHistResult? {
            val info = json.getString("info")
            if (info.equals(BACK_MSG) || info.equals(FORWARD_MSG)) {
                return MoveHistResult(filePositionFromJSON(json.getObject("src_span")),
                        localBindingListFromJSONArray(json.getArray("vars")))
            }
            throw RuntimeException("Wrong json output occured while handling move hist command result")
        }

        public fun expressionTypeFromJSON(json: JSONObject): ExpressionType {
            val info = json.getString("info")
            if (info.equals(EXPRESSION_TYPE_MSG)) {
                return ExpressionType("<unknown>", json.getString("type"))
            } else {
                throw RuntimeException("Wrong JSON output occured while handling expression type command result")
            }
        }

        public fun evalResultFromJSON(json: JSONObject): EvalResult {
            val info = json.getString("info")
            if (info.equals(EVALUATED_MSG)) {
                return EvalResult(json.getString("type"), json.getString("value"))
            } else {
                throw RuntimeException("Wrong JSON output occured while handling expression type command result")
            }
        }

        public fun breaksListFromJSON(json: JSONObject): BreakInfoList {
            val info = json.getString("info")
            if(info.equals(BREAK_LIST_FOR_LINE_INFO)) {
                val indexSpanArray = json.getArray(BREAKS_TAG)
                val lambda = {(p: Any?) -> BreakInfo(
                                           (p as JSONObject).getInt(BREAK_INDEX_TAG),
                                           filePositionFromJSON((p as JSONObject).getObject(SRC_SPAN_TAG))
                                           )}
                return BreakInfoList(ArrayList(indexSpanArray.toArray().map(lambda)))
            } else {
                throw RuntimeException("Wrong JSON output occured while handling expression type command result")
            }
        }

        public fun parseJSONObject(string: String): JSONResult {
            val parser = JSONParser()
            return JSONResult(parser.parse(string) as JSONObject)
        }

        private fun JSONObject.getInt(key: String): Int {
            return (get(key) as Long).toInt()
        }

        private fun JSONObject.getString(key: String): String {
            return get(key) as String
        }

        private fun JSONObject.getObject(key: String): JSONObject {
            return get(key) as JSONObject
        }

        private fun JSONObject.getArray(key: String): JSONArray {
            return get(key) as JSONArray
        }
    }
}