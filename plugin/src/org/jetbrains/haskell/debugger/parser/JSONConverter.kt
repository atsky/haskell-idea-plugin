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
        private val WRONG_OUTPUT_MSG = "Wrong JSON output occured while handling command"
        private val INFO_TAG = "info"
        private val BREAK_LIST_FOR_LINE_INFO = "break list for line"
        private val BREAKS_TAG = "breaks"
        private val BREAK_INDEX_TAG = "index"
        private val SRC_SPAN_TAG = "src_span"

        private val CONNECTED_MSG = "connected to port"

        private val WARNING_MSG = "warning"
        private val EXCEPTION_MSG = "exception"

        private val PAUSED_MSG = "paused"
        private val FINISHED_MSG = "finished"
        private val STOPPED_AT_FUNC_TAG = "function"

        private val BREAKPOINT_SET_MSG = "breakpoint was set"
        private val BREAKPOINT_NOT_SET_MSG = "breakpoint was not set"

        private val BREAKPOINT_REMOVED_MSG = "breakpoint was removed"
        private val BREAKPOINT_NOT_REMOVED_MSG = "breakpoint was not removed"

        private val BACK_MSG = "stepped back"
        private val FORWARD_MSG = "stepped forward"
        private val HISTORY_MSG = "got history"

        private val EXPRESSION_TYPE_MSG = "expression type"

        private val EVALUATED_MSG = "evaluated"

        public fun checkExceptionFromJSON(json: JSONObject): ExceptionResult? {
            val cases: List<Case<ExceptionResult>> = listOf(
                Case({ it.equals(WARNING_MSG) || it.equals(EXCEPTION_MSG) },
                     { ExceptionResult(json.getString("message")) })
            )
            return infoSwitchOrNull(json, cases)
        }

        public fun breakpointCommandResultFromJSON(json: JSONObject): BreakpointCommandResult? {
            val cases: List<Case<BreakpointCommandResult>> = listOf(
                Case({ it.equals(BREAKPOINT_SET_MSG) },
                     { BreakpointCommandResult(json.getInt("index"), filePositionFromJSON(json.getObject("src_span"))!!) }),
                Case({ it.equals(BREAKPOINT_NOT_SET_MSG) },
                     { null: BreakpointCommandResult? })
            )
            return infoSwitchOrThrow(json, cases, WRONG_OUTPUT_MSG + " - set breakpoint")
        }

        public fun stoppedAtFromJSON(json: JSONObject): HsStackFrameInfo? {
            val cases: List<Case<HsStackFrameInfo>> = listOf(
                Case({ it.equals(PAUSED_MSG) },
                     { HsStackFrameInfo(filePositionFromJSON(json.getObject("src_span")),
                                        localBindingListFromJSONArray(json.getArray("vars")).list,
                                        json.getString(STOPPED_AT_FUNC_TAG)) }),
                Case({ it.equals(FINISHED_MSG) },
                     { null: HsStackFrameInfo? })
            )
            return infoSwitchOrThrow(json, cases, WRONG_OUTPUT_MSG + " - flow command")
        }

        public fun moveHistResultFromJSON(json: JSONObject): MoveHistResult? {
            val cases: List<Case<MoveHistResult>> = listOf(
                Case({ it.equals(BACK_MSG) || it.equals(FORWARD_MSG) },
                     { MoveHistResult(filePositionFromJSON(json.getObject("src_span")),
                                      localBindingListFromJSONArray(json.getArray("vars"))) })
            )
            return infoSwitchOrThrow(json, cases, WRONG_OUTPUT_MSG + " - move through history")
        }

        public fun expressionTypeFromJSON(json: JSONObject): ExpressionType {
            val cases: List<Case<ExpressionType>> = listOf(
                Case({ it.equals(EXPRESSION_TYPE_MSG) },
                     { ExpressionType("<unknown>", json.getString("type")) })
            )
            return infoSwitchOrThrow(json, cases, WRONG_OUTPUT_MSG + " - expression type") as ExpressionType
        }

        public fun evalResultFromJSON(json: JSONObject): EvalResult {
            val cases: List<Case<EvalResult>> = listOf(
                Case({ it.equals(EVALUATED_MSG) },
                     { EvalResult(json.getString("type"), json.getString("value")) })
            )
            return infoSwitchOrThrow(json, cases, WRONG_OUTPUT_MSG + " - eval") as EvalResult
        }

        public fun historyResultFromJSON(json: JSONObject): HistoryResult {
            val cases: List<Case<HistoryResult>> = listOf(
                Case({ it.equals(HISTORY_MSG) },
                     { HistoryResult(ArrayList(json.getArray("history").toArray().map {
                                    (callInfo) ->
                                        with (callInfo as JSONObject) {
                                        HsHistoryFrameInfo(getInt("index"), getString("function"),
                                                                  filePositionFromJSON(getObject("src_span")))
                                    }
                                }), json.get("end_reached") as Boolean) })
            )
            return infoSwitchOrThrow(json, cases, WRONG_OUTPUT_MSG + " - get history") as HistoryResult
        }

        public fun breaksListFromJSON(json: JSONObject): BreakInfoList {
            val cases: List<Case<BreakInfoList>> = listOf(
                Case({ it.equals(BREAK_LIST_FOR_LINE_INFO) },
                     { val indexSpanArray = json.getArray(BREAKS_TAG)
                       val lambda = {(p: Any?) -> BreakInfo(
                                     (p as JSONObject).getInt(BREAK_INDEX_TAG),
                                     filePositionFromJSON((p as JSONObject).getObject(SRC_SPAN_TAG)) as HsFilePosition
                                    )}
                       BreakInfoList(ArrayList(indexSpanArray.toArray().map(lambda))) })
            )
            return infoSwitchOrThrow(json, cases, WRONG_OUTPUT_MSG + " - breakpoints list") as BreakInfoList
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

        private fun filePositionFromJSON(json: JSONObject): HsFilePosition? {
            val file = json.get("file")
            if (file != null) {
                return HsFilePosition(file as String, json.getInt("startline"), json.getInt("startcol"),
                        json.getInt("endline"), json.getInt("endcol"))
            }
            return null
        }

        private fun localBindingListFromJSONArray(json: JSONArray): LocalBindingList =
                LocalBindingList(ArrayList(json.toArray().map {
                    (variable) ->
                    with (variable as JSONObject) {
                        LocalBinding(getString("name"), get("type") as String?, get("value") as String?)
                    }
                }))

        private class Case<R: ParseResult>(val condition: (String) -> Boolean, val action: (JSONObject) -> R?)

        private fun infoSwitch<R: ParseResult>(json: JSONObject,
                                               cases: List<Case<R>>,
                                               defaultAction: (JSONObject) -> R?): R? {
            val info = json.getString(INFO_TAG)
            val resultCase = cases firstOrNull { it.condition(info) }
            if(resultCase != null) {
                return resultCase.action(json)
            }
            return defaultAction(json)
        }

        private fun infoSwitchOrThrow<R: ParseResult>(json: JSONObject, cases: List<Case<R>>, throwMsg: String): R? =
                infoSwitch(json, cases, { throw RuntimeException(throwMsg) })

        private fun infoSwitchOrNull<R: ParseResult>(json: JSONObject, cases: List<Case<R>>): R? =
                infoSwitch(json, cases, { null: R? })
    }
}