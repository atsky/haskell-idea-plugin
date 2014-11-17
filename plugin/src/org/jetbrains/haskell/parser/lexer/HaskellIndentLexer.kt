package org.jetbrains.haskell.parser.lexer;

import com.intellij.lang.PsiBuilder
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.lang.WhitespaceSkippedCallback
import java.util.HashSet
import java.util.Arrays
import java.util.ArrayList
import com.intellij.lexer.LexerBase
import com.intellij.lexer.Lexer
import org.jetbrains.grammar.HaskellLexerTokens
import org.jetbrains.haskell.parser.token.NEW_LINE
import org.jetbrains.haskell.parser.token.BLOCK_COMMENT

/**
 * Created by atsky on 26/06/14.
 */


public class HaskellIndentLexer() : LexerBase() {
    val indentTokens = HashSet<IElementType>(Arrays.asList(HaskellLexerTokens.WHERE))

    var buffer: CharSequence? = null
    val tokens: MutableList<IElementType> = ArrayList();
    val text: MutableList<String> = ArrayList();
    val starts: MutableList<Int> = ArrayList()
    val ends: MutableList<Int> = ArrayList()
    var index: Int = 0;


    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer;
        val lexer = HaskellLexer()
        lexer.start(buffer, startOffset, endOffset, initialState);

        var indentStack : IntStack? = null
        var lineStart = 0;
        var firstOnLine = false;
        var writeIndent = false;

        while (lexer.getTokenType() != null) {
            val tokenType = lexer.getTokenType()
            val tokenStart = lexer.getTokenStart()


            if (tokenType != NEW_LINE &&
                tokenType != BLOCK_COMMENT) {
                if (writeIndent) {
                    val indent = tokenStart - lineStart
                    indentStack = IntStack(indent, indentStack)
                    writeIndent = false;
                    firstOnLine = false;

                    tokens.add(HaskellLexerTokens.VOCURLY)
                    starts.add(tokenStart)
                    ends.add(tokenStart)
                    text.add("")
                } else if (firstOnLine) {
                    val indent = tokenStart - lineStart
                    firstOnLine = false;

                    if (indentStack != null) {
                        if (indentStack!!.head == indent) {
                            tokens.add(HaskellLexerTokens.SEMI)
                            starts.add(tokenStart)
                            ends.add(tokenStart)
                            text.add("")
                        }
                        if (indentStack!!.head > indent) {
                            tokens.add(HaskellLexerTokens.VCCURLY)
                            starts.add(tokenStart)
                            ends.add(tokenStart)
                            text.add("")
                            indentStack = indentStack!!.tail
                        }
                    }
                }
            } else {
                if (tokenType == NEW_LINE) {
                    lineStart = lexer.getTokenEnd()
                    firstOnLine = true;
                }
            }


            tokens.add(tokenType)
            starts.add(tokenStart)
            ends.add(lexer.getTokenEnd())
            text.add(lexer.getTokenText())
            lexer.advance()

            if (indentTokens.contains(tokenType)) {
                if (lexer.getTokenType() != HaskellLexerTokens.OCURLY) {
                    writeIndent = true;
                }
            }
        }
        while (indentStack != null) {
            tokens.add(HaskellLexerTokens.VCCURLY)
            starts.add(lexer.getBufferEnd())
            ends.add(lexer.getBufferEnd())
            text.add("")
            indentStack = indentStack!!.tail
        }
        println(tokens)
    }

    override fun getState(): Int {
        throw UnsupportedOperationException()
    }

    override fun getTokenType(): IElementType? {
        return if (index >= 0 && index < tokens.size) tokens[index] else null
    }

    override fun getTokenStart(): Int {
        return starts[index]
    }

    override fun getTokenEnd(): Int {
        return ends[index]
    }

    override fun advance() {
        index++;
    }

    override fun getBufferSequence(): CharSequence {
        throw UnsupportedOperationException()
    }

    override fun getBufferEnd(): Int {
        throw UnsupportedOperationException()
    }

    class IntStack(val head: Int,
                   val tail: IntStack?)
}

