package org.jetbrains.haskell.debugger.parser

import java.util.ArrayList
import org.json.simple.JSONObject
import java.io.File

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

    public val simplePath: String = filePath.substring(if (filePath.contains("/")) filePath.lastIndexOf('/') + 1 else 0)

    public fun spanToString(): String {
        if (rawStartLine == rawEndLine) {
            if (rawStartSymbol == rawEndSymbol) {
                return "$rawStartLine:$rawStartSymbol"
            } else {
                return "$rawStartLine:$rawStartSymbol-$rawEndSymbol"
            }
        } else {
            return "($rawStartLine,$rawStartSymbol)-($rawEndLine,$rawEndSymbol)"
        }
    }
    public fun getFileName(): String = File(filePath).getName()

    override fun toString(): String = "${getFileName()}:${spanToString()}"
}

public class BreakInfo(public val breakIndex: Int, public val srcSpan: HsFilePosition) : ParseResult()
public class BreakInfoList(public val list: ArrayList<BreakInfo>) : ParseResult()

public class ExceptionResult(public val message: String) : ParseResult()

//public class CallInfo(public val index: Int, public val function: String, public val position: FilePosition): ParseResult()
//public class HistoryResult(public val list: ArrayList<CallInfo>) : ParseResult()

public class LocalBinding(var name: String?,
                          var typeName: String?,
                          var value: String?) : ParseResult()

public class LocalBindingList(public val list: ArrayList<LocalBinding>) : ParseResult()

public open class HsStackFrameInfo(val filePosition: HsFilePosition?,
                                   var bindings: ArrayList<LocalBinding>?,
                                   val functionName: String?) : ParseResult()

public class HsHistoryFrameInfo(public val index: Int,
                                public val function: String?,
                                public val filePosition: HsFilePosition?) : ParseResult() {

    override fun toString(): String {
        return "$function : $filePosition"
    }
}

public class ExpressionType(public val expression: String,
                            public val expressionType: String) : ParseResult()

public class EvalResult(public val expressionType: String,
                        public val expressionValue: String) : ParseResult()

public class ShowOutput(public val output: String) : ParseResult()

public class MoveHistResult(public val filePosition: HsFilePosition?,
                            public val bindingList: LocalBindingList) : ParseResult()

public class HistoryResult(public val frames: ArrayList<HsHistoryFrameInfo>,
                           public val full: Boolean) : ParseResult()

public class JSONResult(public val json: JSONObject) : ParseResult()
