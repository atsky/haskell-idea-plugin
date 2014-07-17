package org.jetbrains.haskell.debugger.parser

import java.util.regex.Pattern
import java.io.File
import java.util.ArrayList
import java.util.Deque

/**
 * @author Habibullin Marat
 */

public class Parser() {
    // we can put here functions to parse some known things like 'parseSetBreakpointResult' ect...
    class object {

        private val BREAKPOINT_ACTIVATED_PATTERN = "Breakpoint (\\d+) activated at (.*)"
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
         * Returns line where breakpoint was activated and breakpoint number
         */
        public fun parseSetBreakpointCommandResult(output: Deque<String?>): BreakpointCommandResult {
            val it = output.descendingIterator()
            while (it.hasNext()) {
                val line = it.next()
                val matcher = Pattern.compile(BREAKPOINT_ACTIVATED_PATTERN).matcher(line!!.trim())
                if (matcher.matches()) {
                    val breakpointNumber = matcher.group(1)!!.toInt()
                    val filePositionLine = matcher.group(2)!!
                    val filePosition = tryCreateFilePosition(filePositionLine)
                    if (filePosition != null) {
                        return BreakpointCommandResult(breakpointNumber, filePosition)
                    }
                }
//                val line = it.next()
//                val parts = line!!.split(' ')
//
//                if (parts.size > 4 && parts[0] == "Breakpoint" && parts[2] == "activated" && parts[3] == "at") {
//                    val breakpointNumber = parts[1].toInt()
//                    val lastWord = parts[parts.size - 1]
//                    val lineNumberBegSubstr = lastWord.substring(lastWord.indexOf(':') + 1)
//                    val lineNumber = lineNumberBegSubstr.substring(0, lineNumberBegSubstr.indexOf(':')).toInt()
//                    return BreakpointCommandResult(breakpointNumber, lineNumber)
//                }
            }
            throw RuntimeException("Wrong GHCi output occured while handling SetBreakpointCommand result")
        }

        /**
         * Returns FilePosition, where stopped
         */
        public fun tryParseStoppedAt(output: Deque<String?>): FilePosition? {
            val it = output.descendingIterator()
            while (it.hasNext()) {
                val line = it.next()
                val matcher = Pattern.compile("(.*)" + STOPPED_AT_PATTERN).matcher(line!!.trim())
                if (matcher.matches()) {
                    val str = matcher.group(2)!!
                    val filePosition = tryCreateFilePosition(str)
                    return filePosition
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
                    } else {
                        throw RuntimeException("Wrong GHCi output occured while handling HistoryCommand result")
                    }
                }
            }
            return callStack
        }

        /*
         * Result classes
         */
        public open class ParseResult
        public class BreakpointCommandResult(public val breakpointNumber: Int, public val position: FilePosition) : ParseResult()
        public class FilePosition(public val file: String, public val startLine: Int, public val startSymbol: Int,
                                  public val endLine: Int, public val endSymbol: Int) : ParseResult()
        public class CallInfo(public val index: Int, public val function: String, public val position: FilePosition): ParseResult()
        public class HistoryResult(public val list: ArrayList<CallInfo>) : ParseResult()

    }
}

