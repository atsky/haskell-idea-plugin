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

val INDENT_TOKENS = HashSet<IElementType>(Arrays.asList(
        HaskellLexerTokens.DO,
        HaskellLexerTokens.OF,
        HaskellLexerTokens.LET,
        HaskellLexerTokens.WHERE))

class IntStack(val indent: Int,
               val parent: IntStack?)

public fun getCachedTokens(lexer: HaskellLexer, stream: PrintStream): CachedTokens {
    val tokens = ArrayList<IElementType>()
    val starts = ArrayList<Int>()

    stream.println("-------------------")
    while (lexer.getTokenType() != null) {
        val tokenType = lexer.getTokenType()
        if (tokenType != TokenType.WHITE_SPACE &&
                tokenType != END_OF_LINE_COMMENT &&
                tokenType != BLOCK_COMMENT) {
            tokens.add(tokenType)
            starts.add(lexer.getTokenStart())
            stream.print("${tokenType} ")
        }
        if (tokenType == NEW_LINE) {
            stream.println()
        }
        lexer.advance();
    }
    stream.println("-------------------")
    return CachedTokens(tokens, starts)
}

public fun getCachedTokens(builder: PsiBuilder): CachedTokens {
    val tokens = ArrayList<IElementType>()
    val starts = ArrayList<Int>()

    builder.setWhitespaceSkippedCallback(object : WhitespaceSkippedCallback {
        override fun onSkip(type: IElementType?, start: Int, end: Int) {
            if (type == NEW_LINE) {
                tokens.add(NEW_LINE)
                starts.add(start)
            }
        }

    })

    while (builder.getTokenType() != null) {
        tokens.add(builder.getTokenType())
        starts.add(builder.getCurrentOffset())
        builder.advanceLexer()
    }

    return CachedTokens(tokens, starts)
}

public fun newParserState(tokens: CachedTokens): ParserState {
    return ParserState(tokens, 0, null, null)
}

public class CachedTokens(val tokens: List<IElementType>,
                          val starts: List<Int>) {
}

public class ParserState(val tokens: CachedTokens,
                         val position: Int,
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

    fun next(): ParserState {
        if (currentToken != null && currentToken != HaskellLexerTokens.VCCURLY) {
            return ParserState(tokens,
                    position,
                    null,
                    indentStack)
        }
        if (position == tokens.tokens.size) {
            return ParserState(
                    tokens,
                    position,
                    null,
                    indentStack)
        }
        if (INDENT_TOKENS.contains(tokens.tokens[position])) {
            var curPosition = position + 1
            while (tokens.tokens[curPosition] == NEW_LINE) {
                curPosition++
            }
            if (tokens.tokens[curPosition] == HaskellLexerTokens.OCURLY) {
                return ParserState(
                        tokens,
                        curPosition,
                        null,
                        indentStack)
            }
            val indent = getIndent(curPosition)
            return ParserState(tokens,
                    curPosition,
                    HaskellLexerTokens.VOCURLY,
                    IntStack(indent, indentStack))
        }
        val newPosition = position + 1;
        if (tokens.tokens.size == newPosition) {
            return ParserState(tokens,
                    position + 1,
                    null,
                    indentStack)
        }
        if (tokens.tokens[newPosition] == NEW_LINE) {
            var curPosition = newPosition
            while (curPosition < tokens.tokens.size && tokens.tokens[curPosition] == NEW_LINE) {
                curPosition++
            }
            if (curPosition == tokens.tokens.size) {
                if (indentStack != null) {
                    return ParserState(tokens,
                            position,
                            HaskellLexerTokens.VCCURLY,
                            indentStack.parent)
                } else {
                    return ParserState(tokens,
                            curPosition,
                            null,
                            null)
                }
            }
            val indent = getIndent(curPosition)
            if (indentStack != null) {
                if (indentStack.indent == indent) {
                    return ParserState(tokens, curPosition, HaskellLexerTokens.SEMI, indentStack)
                } else if (indentStack.indent < indent) {
                    return ParserState(tokens, curPosition, null, indentStack)
                } else {
                    return ParserState(tokens, position, HaskellLexerTokens.VCCURLY, indentStack.parent)
                }
            }
            return ParserState(tokens, curPosition, null, indentStack)
        } else {
            return ParserState(tokens, newPosition, null, indentStack)
        }
    }

    fun skip(size: Int): ParserState {
        var current: ParserState = this
        for (i in 1..size) {
            current = current.next()
        }
        return current
    }

    fun getIndent(position: Int): Int {
        var prevNL = position - 1
        while (prevNL >= 0 && tokens.tokens[prevNL] != NEW_LINE) {
            prevNL--
        }
        if (prevNL == -1) {
            return tokens.starts[position]
        }
        return tokens.starts[position] - tokens.starts[prevNL] - 1
    }

    fun dropIndent() = ParserState(
            tokens,
            position,
            HaskellLexerTokens.VCCURLY,
            indentStack!!.parent)

    fun getToken(): IElementType? {
        if (currentToken != null) {
            return currentToken
        }
        if (position < tokens.tokens.size) {
            return tokens.tokens[position];
        }
        return null;
    }


}