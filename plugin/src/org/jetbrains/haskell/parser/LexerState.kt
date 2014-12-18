package org.jetbrains.haskell.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.grammar.HaskellLexerTokens
import java.util.Arrays
import java.util.HashSet
import java.util.ArrayList
import org.jetbrains.haskell.parser.lexer.HaskellLexer
import com.intellij.psi.TokenType
import org.jetbrains.haskell.parser.token.NEW_LINE
import org.jetbrains.haskell.parser.token.END_OF_LINE_COMMENT
import org.jetbrains.haskell.parser.token.BLOCK_COMMENT
import java.io.PrintStream
import com.intellij.lang.PsiBuilder
import com.intellij.lang.WhitespaceSkippedCallback
import org.jetbrains.grammar.dumb.NonTerminalTree
import org.jetbrains.grammar.dumb.TerminalTree
import org.jetbrains.haskell.parser.token.PRAGMA
import org.jetbrains.haskell.parser.token.COMMENTS

val INDENT_TOKENS = HashSet<IElementType>(Arrays.asList(
        HaskellLexerTokens.DO,
        HaskellLexerTokens.OF,
        HaskellLexerTokens.LET,
        HaskellLexerTokens.WHERE))

class IntStack(val indent: Int,
               val parent: IntStack?)

public fun getCachedTokens(lexer: HaskellLexer, stream: PrintStream?): CachedTokens {
    val tokens = ArrayList<IElementType>()
    val starts = ArrayList<Int>()
    val indents = ArrayList<Int>()
    val lineStarts = ArrayList<Boolean>()

    var currentIndent = 0
    var isLineStart = true

    stream?.println("-------------------")
    while (lexer.getTokenType() != null) {
        val tokenType = lexer.getTokenType()
        if (!COMMENTS.contains(tokenType) && tokenType != TokenType.WHITE_SPACE) {
            if (tokenType == NEW_LINE) {
                currentIndent = 0
                isLineStart = true
                stream?.println()
            } else {
                tokens.add(tokenType)
                starts.add(lexer.getTokenStart())
                indents.add(currentIndent)
                lineStarts.add(isLineStart)
                isLineStart = false
                stream?.print("${tokenType} ")
            }
        }

        if (tokenType != NEW_LINE) {
            for (ch in lexer.getTokenText()) {
                if (ch == '\t') {
                    currentIndent += 8;
                } else {
                    currentIndent += 1;
                }
            }
        }
        lexer.advance();
    }
    stream?.println("-------------------")
    return CachedTokens(tokens, starts, indents, lineStarts)
}

public fun getCachedTokens(builder: PsiBuilder): CachedTokens {
    val tokens = ArrayList<IElementType>()
    val starts = ArrayList<Int>()
    val indents = ArrayList<Int>()
    val lineStarts = ArrayList<Boolean>()

    var currentIndent = 0
    var isLineStart = true

    builder.setWhitespaceSkippedCallback(object : WhitespaceSkippedCallback {
        override fun onSkip(type: IElementType?, start: Int, end: Int) {
            if (type == NEW_LINE) {
                currentIndent = 0
                isLineStart = true
            } else {
                for (ch in builder.getOriginalText().subSequence(start, end)!!) {
                    if (ch == '\t') {
                        currentIndent += 8;
                    } else {
                        currentIndent += 1;
                    }
                }
            }
        }

    })

    while (builder.getTokenType() != null) {
        tokens.add(builder.getTokenType())
        starts.add(builder.getCurrentOffset())
        indents.add(currentIndent)
        lineStarts.add(isLineStart)
        isLineStart = false

        currentIndent += builder.getTokenText().size

        builder.advanceLexer()
    }

    return CachedTokens(tokens, starts, indents, lineStarts)
}

public fun newLexerState(tokens: CachedTokens): LexerState {
    return LexerState(tokens, 0, 0, null, null)
}

public class CachedTokens(val tokens: List<IElementType>,
                          val starts: List<Int>,
                          val indents: ArrayList<Int>,
                          val lineStart: ArrayList<Boolean>) {
}

public class LexerState(val tokens: CachedTokens,
                        val position: Int,
                        val lexemNumber: Int,
                        val currentToken: HaskellTokenType?,
                        val indentStack: IntStack?) {

    fun match(token: HaskellTokenType): Boolean {
        if (currentToken != null) {
            return currentToken == token
        }
        if (position < tokens.tokens.size && tokens.tokens[position] == token) {
            return true
        }
        return false
    }

    fun next(): LexerState {
        if (currentToken != null) {
            if (currentToken == HaskellLexerTokens.VCCURLY && indentStack != null) {
                return checkIndent(position)
            }
            return LexerState(tokens, position, lexemNumber + 1, null, indentStack)
        }
        if (position == tokens.tokens.size) {
            return last()
        }
        if (tokens.tokens[position] == HaskellLexerTokens.OCURLY) {
            return LexerState(tokens,
                    position + 1,
                    lexemNumber + 1,
                    null,
                    IntStack(-1, indentStack))
        }
        val nextPosition = position + 1

        if (nextPosition == tokens.tokens.size) {
            return last()
        }

        if (INDENT_TOKENS.contains(tokens.tokens[position]) &&
                tokens.tokens[nextPosition] != HaskellLexerTokens.OCURLY) {

            val indent = tokens.indents[nextPosition]
            return LexerState(tokens,
                    nextPosition,
                    lexemNumber + 1,
                    HaskellLexerTokens.VOCURLY,
                    IntStack(indent, indentStack))
        }

        return checkIndent(nextPosition)
    }

    private fun last(): LexerState {
        if (indentStack != null) {
            return LexerState(tokens,
                    tokens.tokens.size,
                    lexemNumber + 1,
                    HaskellLexerTokens.VCCURLY,
                    indentStack.parent)
        } else {
            return LexerState(tokens, tokens.tokens.size, lexemNumber, null, null)
        }
    }

    private fun checkIndent(position: Int): LexerState {
        if (position == tokens.tokens.size) {
            return last()
        }
        if (tokens.lineStart[position]) {
            val indent = tokens.indents[position]
            if (indentStack != null) {
                if (indentStack.indent == indent) {
                    return LexerState(tokens, position, lexemNumber + 1, HaskellLexerTokens.SEMI, indentStack)
                } else if (indentStack.indent < indent) {
                    return checkCurly(position)
                } else {
                    return LexerState(tokens, position, lexemNumber + 1, HaskellLexerTokens.VCCURLY, indentStack.parent)
                }
            } else {
                //if (0 == indent) {
                //    return LexerState(tokens, position, lexemNumber + 1, HaskellLexerTokens.SEMI, indentStack)
                //} else {
                //    return checkCurly(position)
                //}
            }
        }
        return checkCurly(position)
    }

    private fun checkCurly(nextPosition: Int): LexerState {
        if (tokens.tokens[nextPosition] == HaskellLexerTokens.CCURLY) {
            if (indentStack!!.indent > -1) {
                return LexerState(tokens, nextPosition - 1, lexemNumber + 1, HaskellLexerTokens.VCCURLY, indentStack.parent)
            }
            return LexerState(tokens, nextPosition, lexemNumber + 1, null, indentStack.parent)
        }
        return LexerState(tokens, nextPosition, lexemNumber + 1, null, indentStack)
    }

    fun dropIndent() = LexerState(
            tokens,
            position,
            lexemNumber + 1,
            HaskellLexerTokens.VCCURLY,
            indentStack?.parent)

    fun getToken(): IElementType? {
        if (currentToken != null) {
            return currentToken
        }
        if (position < tokens.tokens.size) {
            return tokens.tokens[position];
        }
        return null;
    }

    fun eof(): Boolean {
        return currentToken == null && position == tokens.tokens.size;
    }


}