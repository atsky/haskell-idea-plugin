package org.jetbrains.haskell.debugger.parser

import java.util.ArrayList

/**
 * This file contains data types for holding parsed information
 *
 * @author Habibullin Marat
 */

public open class ParseResult
public class BreakpointCommandResult(public val breakpointNumber: Int, public val position: FilePosition) : ParseResult()
public class FilePosition(public val filePath: String, public val startLine: Int, public val startSymbol: Int,
                          public val endLine: Int, public val endSymbol: Int) : ParseResult()
public class CallInfo(public val index: Int, public val function: String, public val position: FilePosition): ParseResult()
public class HistoryResult(public val list: ArrayList<CallInfo>) : ParseResult()

public class LocalBinding(val name:String?, val typeName: String?, val value: String?) : ParseResult() {
    class object {
        public val EMPTY_BINDING: LocalBinding = LocalBinding(null, null, null)
    }
}