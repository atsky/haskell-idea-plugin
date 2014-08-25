package org.jetbrains.haskell.debugger.parser

import java.util.regex.Pattern
import java.io.File
import java.util.ArrayList
import java.util.Deque

/**
 * @author Habibullin Marat
 */

public class GHCiParser() {
    // we can put here functions to parse some known things like 'parseSetBreakpointResult' ect...
    class object {
        // the strings above are used as patterns for regexps
        private val BREAKPOINT_ACTIVATED_PATTERN = "Breakpoint (\\d+) activated at (.*)"
        private val BREAKPOINT_NOT_ACTIVATED_PATTERN = "No breakpoints found at that location."
        private val EXCEPTION_BREAKPOINT_PATTERN = "Stopped at <exception thrown>"
        private val CALL_INFO_PATTERN = "-(\\d+)\\s+:\\s(.*)\\s\\((.*)\\)"
        private val STOPPED_AT_PATTERN = "Stopped\\sat\\s(.*)"
        private val LOGGED_BREAKPOINT_AT_PATTERN = "Logged breakpoint\\sat\\s(.*)"
        val FILE_POSITION_PATTERNS = array(
                "(.*):(\\d+):(\\d+)",
                "(.*):(\\d+):(\\d+)-(\\d+)",
                "(.*):\\((\\d+),(\\d+)\\)-\\((\\d+),(\\d+)\\)"
        )
        val POSITION_PATTERN_PLACES = array(
                array(0, 1, 0, 1),
                array(0, 1, 0, 2),
                array(0, 1, 2, 3)
        )

        val LOCAL_BINDING_PATTERN = "([a-zA-Z_]?\\w*)(\\s::\\s)(\\[[\\w\\s\\(\\)]*\\]|[\\w\\s\\(\\)]*)((\\s=\\s\\w*)?)"
        val BINDING_NAME_GROUP = 1
        val BINDING_TYPE_GROUP = 3
        // using LOCAL_BINDING_PATTERN above, one get substring ' = <value>' in one group. We need only value so
        // BINDING_VALUE_PRECEDING_SUBSTR is used to find where in substring is binding's value located
        val BINDING_VALUE_CONTAINING_GROUP = 4
        val BINDING_VALUE_PRECEDING_SUBSTR = "="

        val NO_MORE_BREAKPOINTS_PATTERN = "no more logged breakpoints"

        val EXPRESSION_TYPE_PATTERN = "(.*) :: (.*)"

        val SHOW_RESULT_PATTERN = "\"(.*)\""

        private val FORCE_OUTPUT_PATTERN = "^(\\w+)\\s=\\s(.*)$"

        private fun tryCreateFilePosition(line: String): HsFilePosition? {
            for (i in 0..(FILE_POSITION_PATTERNS.size - 1)) {
                val matcher = Pattern.compile(FILE_POSITION_PATTERNS[i]).matcher(line)
                if (matcher.matches()) {
                    val path = matcher.group(1)!!
                    val values = IntArray(matcher.groupCount() - 1)
                    for (j in 0..(values.size - 1)) {
                        values[j] = Integer.parseInt(matcher.group(j + 2)!!)
                    }
                    return HsFilePosition(path, values[POSITION_PATTERN_PLACES[i][0]], values[POSITION_PATTERN_PLACES[i][1]],
                            values[POSITION_PATTERN_PLACES[i][2]], values[POSITION_PATTERN_PLACES[i][3]])
                }
            }
            return null;
        }

        /**
         * Returns line where breakpoint was activated and breakpoint number, null if not activated
         */
        public fun parseSetBreakpointCommandResult(output: Deque<String?>): BreakpointCommandResult? {
            val it = output.descendingIterator()
            while (it.hasNext()) {
                val line = it.next()!!
                val matcher1 = Pattern.compile(BREAKPOINT_ACTIVATED_PATTERN).matcher(line.trim())
                val matcher2 = Pattern.compile(BREAKPOINT_NOT_ACTIVATED_PATTERN).matcher(line.trim())
                if (matcher1.matches()) {
                    val breakpointNumber = matcher1.group(1)!!.toInt()
                    val filePositionLine = matcher1.group(2)!!
                    val filePosition = tryCreateFilePosition(filePositionLine)
                    if (filePosition != null) {
                        return BreakpointCommandResult(breakpointNumber, filePosition)
                    }
                } else if (matcher2.matches()) {
                    return null;
                }
            }
            throw RuntimeException("Wrong GHCi output occured while handling SetBreakpointCommand result")
        }

        /**
         * Parses ghci output that appears on reaching some position in file under debugging (after commands :continue,
         * :trace, :step, :steplocal).
         */
        public fun tryParseStoppedAt(output: Deque<String?>): HsStackFrameInfo? {
            val it = output.descendingIterator()
            var filePosition: HsFilePosition?
            val localBindings = ArrayList<LocalBinding>()
            var res: LocalBinding?
            while (it.hasNext()) {
                val currentLine = it.next()
                val matcher0 = Pattern.compile("(.*)" + EXCEPTION_BREAKPOINT_PATTERN).matcher(currentLine!!.trim())
                if (matcher0.matches()) {
                    return HsStackFrameInfo(null, localBindings, null)
                }
                filePosition = tryParseFilePosition(currentLine.trim(), STOPPED_AT_PATTERN)
                if (filePosition != null) {
                    return HsStackFrameInfo(filePosition as HsFilePosition, localBindings, null)
                }
                res = tryParseLocalBinding(currentLine.trim())
                if (res != null) {
                    localBindings.add(res as LocalBinding)
                }
            }
            return null
        }

        /**
         * Parses ghci output trying to find local bindings in it
         */
        private fun tryParseLocalBindings(output: Deque<String?>): LocalBindingList {
            val localBindings = ArrayList<LocalBinding>()
            var res: LocalBinding?
            for (currentLine in output) {
                res = tryParseLocalBinding(currentLine?.trim())
                if (res != null) {
                    localBindings.add(res as LocalBinding)
                }
            }
            return LocalBindingList(localBindings)
        }

        public fun parseMoveHistResult(output: Deque<String?>): MoveHistResult? {
            var position: String
            val line = output.pollFirst()!!
            val matcher1 = Pattern.compile(STOPPED_AT_PATTERN).matcher(line.trim())
            val matcher2 = Pattern.compile(LOGGED_BREAKPOINT_AT_PATTERN).matcher(line.trim())
            if (matcher1.matches()) {
                position = matcher1.group(1)!!
            } else if (matcher2.matches()) {
                position = matcher2.group(1)!!
            } else {
                return null
            }
            val list = tryParseLocalBindings(output)
            return MoveHistResult(tryCreateFilePosition(position), list)
        }

        private fun tryParseFilePosition(string: String?, pattern: String): HsFilePosition? {
            if (string != null) {
                val matcher = Pattern.compile("(.*)" + pattern).matcher(string)
                if (matcher.matches()) {
                    val str = matcher.group(2)!!
                    return tryCreateFilePosition(str)
                }
            }
            return null
        }

        /**
         * GHCi returns some output wrapped in special modifiers to make text bold in console. This method removes these
         * modifiers (we no need them) and returns "clear" text
         */
        private fun removeBoldModifier(boldText: String): String {
            val boldStartTag = "\u001B[1m"
            val boldEndTag = "\u001B[0m"
            val startIndex = if (boldText.startsWith(boldStartTag)) boldStartTag.size else 0
            val endIndex = if (boldText.endsWith(boldEndTag)) boldText.size - boldEndTag.size else boldText.size
            return boldText.substring(startIndex, endIndex)
        }

        private fun tryParseLocalBinding(string: String?): LocalBinding? {
            if (string != null) {
                val matcher = Pattern.compile(LOCAL_BINDING_PATTERN).matcher(string)
                if (matcher.matches()) {
                    val name = matcher.group(BINDING_NAME_GROUP)
                    val typeName = matcher.group(BINDING_TYPE_GROUP)
                    val substrWithValue = matcher.group(BINDING_VALUE_CONTAINING_GROUP)
                    var value: String? = null
                    if (substrWithValue != null && !substrWithValue.isEmpty()) {
                        value = substrWithValue.substring(substrWithValue.indexOf(BINDING_VALUE_PRECEDING_SUBSTR) + 1).trim()
                    }
                    return LocalBinding(name, typeName, value)
                }
            }
            return null
        }

        public fun parseExpressionType(string: String): ExpressionType? {
            val matcher = Pattern.compile(EXPRESSION_TYPE_PATTERN).matcher(string.trim())
            if (matcher.matches()) {
                return ExpressionType(matcher.group(1)!!, matcher.group(2)!!)
            }
            return null
        }

        public fun tryParseShowOutput(output: Deque<String?>): ShowOutput? {
            val line = output.firstOrNull()
            if (line != null) {
                val matcher = Pattern.compile(SHOW_RESULT_PATTERN).matcher(line)
                if (matcher.matches()) {
                    return ShowOutput(matcher.group(1)!!)
                }
            }
            return null;
        }

        public fun parseHistoryResult(output: Deque<String?>): HistoryResult {
            var full = false
            val list = ArrayList<HsHistoryFrameInfo>()
            for (line in output) {
                if (line!!.trim().equals("<end of history>") || line.trim().equals("Empty history. Perhaps you forgot to use :trace?")) {
                    full = true
                    break
                }
                val matcher = Pattern.compile(CALL_INFO_PATTERN).matcher(line.trim())
                if (matcher.matches()) {
                    list.add(HsHistoryFrameInfo(-matcher.group(1)!!.toInt(), removeBoldModifier(matcher.group(2)!!),
                            tryCreateFilePosition(matcher.group(3)!!)))
                }
            }
            return HistoryResult(list, full)
        }

        public fun tryParseAnyPrintCommandOutput(output: Deque<String?>): LocalBinding? {
            for (line in output) {
                if (line != null) {
                    val matcher = Pattern.compile(FORCE_OUTPUT_PATTERN).matcher(line)
                    if (matcher.matches()) {
                        return LocalBinding(matcher.group(1), null, matcher.group(2))
                    }
                }
            }
            return null
        }
    }
}

