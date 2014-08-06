package org.jetbrains.haskell.debugger.parser

import java.util.ArrayList
import org.json.simple.JSONObject

/**
 * This file contains data types for holding parsed information
 *
 * @author Habibullin Marat
 */

public open class ParseResult

public class BreakpointCommandResult(public val breakpointNumber: Int,
                                     public val position: HsFilePosition) : ParseResult()

public class HsFilePosition(public val filePath: String,
                            public val rawStartLine: Int,
                            public val rawStartSymbol: Int,
                            public val rawEndLine: Int,
                            public val rawEndSymbol: Int)
: ParseResult() {
    // zero based start line number
    public val normalizedStartLine: Int = rawStartLine - 1
    public val normalizedStartSymbol: Int = rawStartSymbol
    // zero based end line number
    public val normalizedEndLine: Int = rawEndLine - 1
    // ghci returns value for end symbol that is less for 1 than idea uses. so normalizedEndSymbol contains corrected one
    public val normalizedEndSymbol: Int = rawEndSymbol + 1

    override fun toString(): String {
        if (rawStartLine == rawEndLine) {
            if (rawStartSymbol == rawEndSymbol) {
                return "$filePath:$rawStartLine:$rawStartSymbol"
            } else {
                return "$filePath:$rawStartLine:$rawStartSymbol-$rawEndSymbol"
            }
        } else {
            return "$filePath:($rawStartLine,$rawStartSymbol)-($rawEndLine,$rawEndSymbol)"
        }
    }
}

public class ExceptionResult(public val message: String) : ParseResult()

//public class CallInfo(public val index: Int, public val function: String, public val position: FilePosition): ParseResult()
//public class HistoryResult(public val list: ArrayList<CallInfo>) : ParseResult()

public class LocalBinding(var name: String?,
                          var typeName: String?,
                          var value: String?) : ParseResult()

public open class HsStackFrameInfo(val filePosition: HsFilePosition,
                                   var bindings: ArrayList<LocalBinding>?) : ParseResult()

public class ExpressionType(public val expression: String,
                            public val expressionType: String) : ParseResult()

public class EvalResult(public val expressionType: String,
                        public val expressionValue: String) : ParseResult()

public class ShowOutput(public val output: String) : ParseResult()

public class LocalBindingList(public val list: ArrayList<LocalBinding>) : ParseResult()

public class MoveHistResult(public val filePosition: HsFilePosition,
                            public val bindingList: LocalBindingList,
                            public val topHist: Boolean,
                            public val botHist: Boolean) : ParseResult()

public class JSONResult(public val json: JSONObject) : ParseResult()
