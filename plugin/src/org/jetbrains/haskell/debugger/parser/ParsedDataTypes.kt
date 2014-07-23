package org.jetbrains.haskell.debugger.parser

import java.util.ArrayList

/**
 * This file contains data types for holding parsed information
 *
 * @author Habibullin Marat
 */

public open class ParseResult

public class BreakpointCommandResult(public val breakpointNumber: Int,
                                     public val position: FilePosition) : ParseResult()

public class FilePosition(public val filePath: String,
                          public val startLine: Int,
                          public val startSymbol: Int,
                          public val endLine: Int,
                          public val endSymbol: Int) : ParseResult()

//public class CallInfo(public val index: Int, public val function: String, public val position: FilePosition): ParseResult()
//public class HistoryResult(public val list: ArrayList<CallInfo>) : ParseResult()

public class LocalBinding(val name: String?,
                          val typeName: String?,
                          val value: String?) : ParseResult() {
    class object {
        public val EMPTY_BINDING: LocalBinding = LocalBinding(null, null, null)
    }
}

public open class HsTopStackFrameInfo(val filePosition: FilePosition,
                                      var bindings: ArrayList<LocalBinding>?) : ParseResult()

public class HsCommonStackFrameInfo(val index: Int,
                                    val functionName: String,
                                    filePosition: FilePosition,
                                    bindings: ArrayList<LocalBinding>?) : HsTopStackFrameInfo(filePosition, bindings)

public class ExpressionType(public val expression: String,
                            public val expressionType: String) : ParseResult()

public class Plain(public val output: String) : ParseResult()

public class History(public val list: ArrayList<HsCommonStackFrameInfo>) : ParseResult()

public class LocalBindingList(public val list: ArrayList<LocalBinding>) : ParseResult()