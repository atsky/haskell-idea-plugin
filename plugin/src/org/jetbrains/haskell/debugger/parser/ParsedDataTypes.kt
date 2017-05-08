package org.jetbrains.haskell.debugger.parser

import java.util.ArrayList
import org.json.simple.JSONObject
import java.io.File

/**
 * This file contains data types for holding parsed information
 *
 * @author Habibullin Marat
 */

open class ParseResult

class BreakpointCommandResult(val breakpointNumber: Int,
                                     val position: HsFilePosition) : ParseResult() {

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as BreakpointCommandResult
        return breakpointNumber == othCasted.breakpointNumber && position.equals(othCasted.position)
    }
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + breakpointNumber
        result = prime * result + position.hashCode()
        return result
    }
}

class HsFilePosition(val filePath: String,
                            val rawStartLine: Int,
                            val rawStartSymbol: Int,
                            val rawEndLine: Int,
                            val rawEndSymbol: Int)
: ParseResult() {
    // zero based start line number
    val normalizedStartLine: Int = rawStartLine - 1
    val normalizedStartSymbol: Int = rawStartSymbol
    // zero based end line number
    val normalizedEndLine: Int = rawEndLine - 1
    // ghci returns value for end symbol that is less for 1 than idea uses. so normalizedEndSymbol contains corrected one
    val normalizedEndSymbol: Int = rawEndSymbol + 1

    val simplePath: String = filePath.substring(if (filePath.contains("/")) filePath.lastIndexOf('/') + 1 else 0)

    fun spanToString(): String {
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
    fun getFileName(): String = File(filePath).name

    override fun toString(): String = "${getFileName()}:${spanToString()}"

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as HsFilePosition
        return filePath.equals(othCasted.filePath) && rawStartLine == othCasted.rawStartLine &&
                rawEndLine == othCasted.rawEndLine && rawStartSymbol == othCasted.rawStartSymbol &&
                rawEndSymbol == othCasted.rawEndSymbol
    }
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + filePath.hashCode()
        result = prime * result + rawStartLine
        result = prime * result + rawEndLine
        result = prime * result + rawStartSymbol
        result = prime * result + rawEndSymbol
        return result
    }
}

class BreakInfo(val breakIndex: Int, val srcSpan: HsFilePosition) : ParseResult()
class BreakInfoList(val list: ArrayList<BreakInfo>) : ParseResult()

class ExceptionResult(val message: String) : ParseResult()

//public class CallInfo(public val index: Int, public val function: String, public val position: FilePosition): ParseResult()
//public class HistoryResult(public val list: ArrayList<CallInfo>) : ParseResult()

class LocalBinding(var name: String?,
                          var typeName: String?,
                          var value: String?) : ParseResult() {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as LocalBinding
        return name == othCasted.name && typeName == othCasted.typeName && value == othCasted.value
    }
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (name?.hashCode() ?: 0)
        result = prime * result + (typeName?.hashCode() ?: 0)
        result = prime * result + (value?.hashCode() ?: 0)
        return result
    }
}

class LocalBindingList(val list: List<LocalBinding>) : ParseResult() {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as LocalBindingList
        var bindingsAreEq = true
        if (list.size != othCasted.list.size) {
            bindingsAreEq = false
        } else {
            for (i in 0..list.size - 1) {
                if (list.get(i) != othCasted.list.get(i)) {
                    bindingsAreEq = false
                    break
                }
            }
        }
        return bindingsAreEq
    }
}

open class HsStackFrameInfo(val filePosition: HsFilePosition?,
                                   var bindings: List<LocalBinding>?,
                                   val functionName: String?) : ParseResult() {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as HsStackFrameInfo
        var bindingsAreEq = true
        if (bindings != null && othCasted.bindings != null && bindings!!.size != othCasted.bindings!!.size) {
            bindingsAreEq = false
        } else {
            for (i in 0..bindings!!.size - 1) {
                if (bindings!!.get(i) != othCasted.bindings!!.get(i)) {
                    bindingsAreEq = false
                    break
                }
            }
        }
        return bindingsAreEq && filePosition == othCasted.filePosition && functionName == othCasted.functionName
    }
}

class HsHistoryFrameInfo(val index: Int,
                                val function: String?,
                                val filePosition: HsFilePosition?) : ParseResult() {

    override fun toString(): String {
        return if (function != null) "$function : $filePosition" else filePosition.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as HsHistoryFrameInfo
        return index == othCasted.index && function == othCasted.function && filePosition == othCasted.filePosition
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + index
        result = prime * result + (function?.hashCode() ?: 0)
        result = prime * result + (filePosition?.hashCode() ?: 0)
        return result
    }
}

class ExpressionType(val expression: String,
                            val expressionType: String) : ParseResult() {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as ExpressionType
        return expression == othCasted.expression && expressionType == othCasted.expressionType
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + expression.hashCode()
        result = prime * result + expressionType.hashCode()
        return result
    }
}

class EvalResult(val expressionType: String,
                        val expressionValue: String) : ParseResult()

class ShowOutput(val output: String) : ParseResult()

class MoveHistResult(val filePosition: HsFilePosition?,
                            val bindingList: LocalBindingList) : ParseResult() {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as MoveHistResult
        return filePosition == othCasted.filePosition && bindingList == othCasted.bindingList
    }
}

class HistoryResult(val frames: List<HsHistoryFrameInfo>,
                           val full: Boolean) : ParseResult() {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val othCasted = other as HistoryResult
        var framesAreEq = true
        if (frames.size != othCasted.frames.size) {
            framesAreEq = false
        } else {
            for (i in 0..frames.size - 1) {
                if (frames.get(i) != othCasted.frames.get(i)) {
                    framesAreEq = false
                    break
                }
            }
        }
        return framesAreEq && full == othCasted.full
    }
}

class JSONResult(val json: JSONObject) : ParseResult()
