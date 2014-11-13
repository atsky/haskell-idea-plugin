package org.jetbrains.haskell.parser.rules

import com.intellij.lang.PsiBuilder
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.lang.WhitespaceSkippedCallback
import java.util.HashSet
import java.util.Arrays
import java.util.ArrayList
import generated.GeneratedParser
import generated.GeneratedTypes
import com.intellij.lexer.LexerBase
import com.intellij.lexer.Lexer
import lexer.KitHaskellLexer

/**
 * Created by atsky on 26/06/14.
 */




public class HaskellIndentLexer() : LexerBase() {
    val indentTokens = HashSet<IElementType>(Arrays.asList(GeneratedTypes.WHEREID))

    var buffer: CharSequence? = null
    val tokens: MutableList<IElementType> = ArrayList();
    val text: MutableList<String> = ArrayList();
    val starts: MutableList<Int> = ArrayList()
    val ends: MutableList<Int> = ArrayList()
    var index: Int = 0;


    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer;
        val lexer = KitHaskellLexer()
        lexer.start(buffer, startOffset, endOffset, initialState);
        while (lexer.getTokenType() != null) {
            tokens.add(lexer.getTokenType())
            starts.add(lexer.getTokenStart())
            ends.add(lexer.getTokenEnd())
            text.add(lexer.getTokenText())
            lexer.advance()
        }
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
}

