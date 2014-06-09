package org.jetbrains.haskell.parser.lexer

import com.intellij.lexer.Lexer
import com.intellij.psi.tree.IElementType
import com.intellij.lexer.LexerPosition
import java.util.ArrayList
import com.intellij.lexer.LexerBase
import java.util.LinkedList
import java.util.Collections
import java.util.HashSet
import java.util.Arrays
import com.intellij.psi.TokenType
import org.jetbrains.haskell.parser.token.*

/**
 * @author Evgeny.Kurbatsky
 */
public class HaskellFullLexer() : LexerBase() {
    val indentTokens = HashSet<IElementType>(Arrays.asList(
            DO_KW,
            OF_KW,
            LET_KW,
            WHERE_KW))

    val lexer = HaskellLexer()
    val tokenStarts = ArrayList<Int>();
    val tokenEnds = ArrayList<Int>();
    val lexerStates = ArrayList<Int>();
    val tokenTypes = ArrayList<IElementType>();
    var currentPosition = 0;

    public override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        lexer.start(buffer, startOffset, endOffset, initialState)
        val indentStack = LinkedList<Int>()
        var lineStart: Int = 0
        var lineStarted: Boolean = false
        var recordIndent: Boolean = false

        while (true) {
            val tokenType = lexer.getTokenType()
            if (tokenType == null) {
                break
            }

            if (tokenType == NEW_LINE) {
                lineStart = lexer.getTokenStart() + 1
                lineStarted = true;
            }

            if (!WHITESPACES.contains(tokenType) && !COMMENTS.contains(tokenType)) {
                val indentSize: Int = lexer.getTokenStart() - lineStart

                if (recordIndent) {
                    if (tokenType != LEFT_BRACE && (indentStack.isEmpty() || indentSize > indentStack.last!!)) {
                        indentStack.addLast(indentSize);
                        addToken(lexer.getTokenStart(), lexer.getTokenStart(), 0, VIRTUAL_LEFT_PAREN)
                    }
                    recordIndent = false
                } else if (lineStarted) {
                    if (indentStack.isNotEmpty() && indentSize == indentStack.last!!) {
                        addToken(lexer.getTokenStart(), lexer.getTokenStart(), 0, VIRTUAL_SEMICOLON)
                    }

                    if (indentStack.isNotEmpty() && indentSize < indentStack.last!!) {
                        while (indentSize < indentStack.last!!) {
                            indentStack.removeLast();
                            addToken(lexer.getTokenStart(), lexer.getTokenStart(), 0, VIRTUAL_RIGHT_PAREN)
                        }
                        addToken(lexer.getTokenStart(), lexer.getTokenStart(), 0, VIRTUAL_SEMICOLON)
                    }
                }
                lineStarted = false
            }

            if (indentTokens.contains(tokenType)) {
                recordIndent = true;
            }

            addToken(lexer.getTokenStart(), lexer.getTokenEnd(), lexer.getState(), tokenType)
            lexer.advance()
        }
        while (indentStack.size > 0) {
            addToken(tokenEnds[tokenEnds.size - 1], tokenEnds[tokenEnds.size - 1], 0, VIRTUAL_RIGHT_PAREN)
            indentStack.removeLast()
        }
    }

    fun addToken(start: Int, end: Int, state: Int, aType: IElementType) {
        tokenStarts.add(start)
        tokenEnds.add(end)
        lexerStates.add(state)
        tokenTypes.add(aType)
    }

    public override fun getState(): Int {
        return lexerStates[currentPosition];
    }

    public override fun getTokenType(): IElementType? {
        return if (tokenTypes.size > currentPosition) tokenTypes[currentPosition] else null
    }

    public override fun getTokenStart(): Int {
        return tokenStarts[currentPosition];
    }

    public override fun getTokenEnd(): Int {
        return tokenEnds[currentPosition]
    }
    public override fun advance() {
        currentPosition++;
    }

    public override fun restore(position: LexerPosition) {
        throw UnsupportedOperationException()
    }
    public override fun getBufferSequence(): CharSequence {
        return lexer.getBufferSequence()
    }

    public override fun getBufferEnd(): Int {
        return lexer.getBufferEnd()
    }

}