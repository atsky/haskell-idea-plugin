package org.jetbrains.haskell.debugger.parser

import java.util.regex.Pattern
import java.io.File
import java.util.ArrayList
import java.util.Deque
import org.jetbrains.haskell.debugger.frames.HaskellStackFrameInfo

/**
 * @author Habibullin Marat
 */

public class Parser() {
    // we can put here functions to parse some known things like 'parseSetBreakpointResult' ect...
    class object {
        // the strings above are used as patterns for regexps
        private val BREAKPOINT_ACTIVATED_PATTERN = "Breakpoint (\\d+) activated at (.*)"
        private val BREAKPOINT_NOT_ACTIVATED_PATTERN = "No breakpoints found at that location."
        private val CALL_INFO_PATTERN = "-(\\d+)\\s+:\\s(.*)\\s\\((.*)\\)"
        private val STOPPED_AT_PATTERN = "Stopped\\sat\\s(.*)"
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
        val LOCAL_BINDING_PATTERN = "([a-zA-Z_]?\\w*)(\\s::\\s)(\\[\\w*\\]|\\w*)((\\s=\\s\\w*)?)"
        val BINDING_NAME_GROUP = 1
        val BINDING_TYPE_GROUP = 3
        // using LOCAL_BINDING_PATTERN above, one get substring ' = <value>' in one group. We need only value so
        // BINDING_VALUE_PRECEDING_SUBSTR is used to find where in substring is binding's value located
        val BINDING_VALUE_CONTAINING_GROUP = 4
        val BINDING_VALUE_PRECEDING_SUBSTR = "="

        public fun tryCreateFilePosition(line: String): FilePosition? {
            for (i in 0..(FILE_POSITION_PATTERNS.size - 1)) {
                val matcher = Pattern.compile(FILE_POSITION_PATTERNS[i]).matcher(line)
                if (matcher.matches()) {
                    val path = matcher.group(1)!!
                    if (!File(path).exists()) {
                        return null;
                    }
                    val values = IntArray(matcher.groupCount() - 1)
                    for (j in 0..(values.size - 1)) {
                        values[j] = Integer.parseInt(matcher.group(j + 2)!!)
                    }
                    return FilePosition(path, values[POSITION_PATTERN_PLACES[i][0]], values[POSITION_PATTERN_PLACES[i][1]],
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
         * :trace, :step, :steplocal). Output may contain some program output at the beginning so we parse lines in
         * reversed order
         *
         * @return stack frame info containing info about position in file and local bindings list
         */
        public fun tryParseStoppedAt(output: Deque<String?>): HaskellStackFrameInfo? {
            val it = output.descendingIterator()
            var filePosition: FilePosition?
            val localBindings = ArrayList<LocalBinding>()
            while (it.hasNext()) {
                val currentLine = it.next()
                filePosition = tryParseFilePosition(currentLine?.trim())
                if(filePosition != null) {
                    return HaskellStackFrameInfo(filePosition as FilePosition, localBindings)
                }
                val res = tryParseLocalBinding(currentLine?.trim())
                if(res != null) {
                    localBindings.add(res)
                }
            }
            return null
        }


        public fun parseHistory(output: Deque<String?>): HistoryResult {
            val callStack = HistoryResult(ArrayList<CallInfo>())
            for (line in output) {
                if (line?.trim().equals("<end of history>")) {
                    break
                } else {
                    val matcher = Pattern.compile(CALL_INFO_PATTERN).matcher(line!!.trim())
                    if (matcher.matches()) {
                        val index = -Integer.parseInt(matcher.group(1)!!)
                        val function = matcher.group(2)!!
                        val filePositionLine = matcher.group(3)!!
                        val filePosition = tryCreateFilePosition(filePositionLine)
                        if (filePosition == null) {
                            throw RuntimeException("Wrong GHCi output occured while handling HistoryCommand result")
                        }
                        callStack.list.add(CallInfo(index, function, filePosition))
                    }
                }
            }
            return callStack
        }

        private fun tryParseFilePosition(string: String?): FilePosition? {
            if(string != null) {
                val matcher = Pattern.compile("(.*)" + STOPPED_AT_PATTERN).matcher(string)
                if (matcher.matches()) {
                    val str = matcher.group(2)!!
                    return tryCreateFilePosition(str)
                }
            }
            return null
        }

        private fun tryParseLocalBinding(string: String?): LocalBinding? {
            if(string != null) {
                val matcher = Pattern.compile(LOCAL_BINDING_PATTERN).matcher(string)
                if (matcher.matches()) {
                    val name = matcher.group(BINDING_NAME_GROUP)
                    val typeName = matcher.group(BINDING_TYPE_GROUP)
                    val substrWithValue = matcher.group(BINDING_VALUE_CONTAINING_GROUP)
                    var value: String? = null
                    if(substrWithValue != null) {
                        value = substrWithValue.substring(substrWithValue.indexOf(BINDING_VALUE_PRECEDING_SUBSTR))
                    }
                    return LocalBinding(name, typeName, value)
                }
            }
            return null
        }
    }
}

