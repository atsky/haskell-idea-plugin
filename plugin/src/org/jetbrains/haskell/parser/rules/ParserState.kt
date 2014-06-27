package org.jetbrains.haskell.parser.rules

import com.intellij.lang.PsiBuilder
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.parser.token.*
import com.intellij.lang.WhitespaceSkippedCallback
import java.util.HashSet
import java.util.Arrays
import org.jetbrains.haskell.parser.HaskellToken
import java.util.ArrayList
import org.jetbrains.haskell.parser.rules.ParserState.IntStack

/**
 * Created by atsky on 26/06/14.
 */




public class ParserState(val build: PsiBuilder) {
    val indentTokens = HashSet<IElementType>(Arrays.asList(
            DO_KW,
            OF_KW,
            LET_KW,
            WHERE_KW))

    var lineStart : Int = 0
    var tokenStack : HaskellToken? = null
    var isOnNewLine : Boolean = true
    var indentStack : IntStack? = null;

    {
        build.setWhitespaceSkippedCallback(object : WhitespaceSkippedCallback {
            override fun onSkip(tokenType: IElementType?, start: Int, end: Int) {
                if (tokenType == NEW_LINE) {
                    lineStart = end
                    isOnNewLine = true
                }
            }

        })
    }

    fun mark(): ParserMarker {
        return ParserMarker(build.mark()!!)
    }

    fun getTokenType(): IElementType? {
        if (tokenStack != null) {
            return tokenStack
        } else {
            isOnNewLine = false
            return build.getTokenType()
        }

    }

    fun advanceLexer() {
        if (tokenStack != null) {
            tokenStack = null
            return
        }
        if (indentTokens.contains(build.getTokenType())) {
            build.advanceLexer()
            build.getTokenType()
            tokenStack = VIRTUAL_LEFT_PAREN
            indentStack = IntStack(build.getCurrentOffset() - lineStart, indentStack)
        } else {
            build.advanceLexer()
            if (build.getTokenType() == null) {
                return
            }
            if (isOnNewLine) {
                val offset = build.getCurrentOffset() - lineStart
                if (indentStack != null && indentStack!!.head == offset) {
                    tokenStack = VIRTUAL_SEMICOLON
                }
                System.out.println(offset)
            }
        }

    }

    fun eof(): Boolean {
        return build.eof()
    }

    fun popIndent() {
        indentStack = indentStack?.tail
    }

    inner class ParserMarker(val marker: PsiBuilder.Marker) {
        val lineStart_ : Int = lineStart
        val tokenStack_ : HaskellToken? = tokenStack
        val isOnNewLine_ : Boolean = isOnNewLine
        val indentStack_ : IntStack? = indentStack


        fun done(elementType : IElementType) {
            marker.done(elementType)
        }

        fun drop() {
            marker.drop()
        }

        fun rollbackTo() {
            lineStart = lineStart_
            tokenStack = tokenStack_
            isOnNewLine = isOnNewLine_
            indentStack = indentStack_
            marker.rollbackTo()
        }
    }

    class IntStack(val head : Int,
                   val tail : IntStack?)
}

