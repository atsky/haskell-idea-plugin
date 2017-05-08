package org.jetbrains.haskell.highlight

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.options.OptionsBundle
import com.intellij.openapi.util.Pair
import com.intellij.psi.StringEscapesTokenTypes
import com.intellij.psi.tree.IElementType
import gnu.trove.THashMap
import org.jetbrains.haskell.parser.lexer.*
import org.jetbrains.haskell.parser.HaskellTokenType
import java.awt.*
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.grammar.HaskellLexerTokens


open class HaskellHighlighter : SyntaxHighlighterBase() {

    companion object {
        val HASKELL_BRACKETS: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_BRACKETS", DefaultLanguageHighlighterColors.BRACES)
        val HASKELL_CLASS: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_CLASS")
        val HASKELL_CONSTRUCTOR: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_CONSTRUCTOR")
        val HASKELL_CURLY: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_CURLY", DefaultLanguageHighlighterColors.BRACES)
        val HASKELL_DOUBLE_COLON: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_DOUBLE_COLON")
        val HASKELL_EQUAL: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_EQUAL", DefaultLanguageHighlighterColors.IDENTIFIER)
        val HASKELL_KEYWORD: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val HASKELL_PARENTHESIS: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_PARENTHESIS", DefaultLanguageHighlighterColors.PARENTHESES)
        val HASKELL_STRING_LITERAL: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_STRING_LITERAL", DefaultLanguageHighlighterColors.STRING)
        val HASKELL_SIGNATURE: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_SIGNATURE")
        val HASKELL_COMMENT: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val HASKELL_PRAGMA: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_PAGMA", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val HASKELL_NUMBER: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val HASKELL_TYPE: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_TYPE")
        val HASKELL_OPERATOR: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_OPERATOR", DefaultLanguageHighlighterColors.IDENTIFIER)
        val HASKELL_IDENTIFIER: TextAttributesKey
                = TextAttributesKey.createTextAttributesKey("HASKELL_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
    }

    override fun getHighlightingLexer(): Lexer {
        return HaskellLexer()
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return SyntaxHighlighterBase.pack(keys1.get(tokenType))
    }


    private val keys1: MutableMap<IElementType, TextAttributesKey>



    val DISPLAY_NAMES: MutableMap<TextAttributesKey, Pair<String, HighlightSeverity>> = THashMap<TextAttributesKey, Pair<String, HighlightSeverity>>(4)

    init {
        keys1 = THashMap<IElementType, TextAttributesKey>()
        keys1.put(END_OF_LINE_COMMENT, HASKELL_COMMENT)
        keys1.put(BLOCK_COMMENT, HASKELL_COMMENT)

        for (keyword in org.jetbrains.haskell.parser.token.KEYWORDS)
        {
            keys1.put(keyword, HASKELL_KEYWORD)
        }
        keys1.put(PRAGMA, HASKELL_PRAGMA)
        // PARENTHESIS
        keys1.put(HaskellLexerTokens.OPAREN, HASKELL_PARENTHESIS)
        keys1.put(HaskellLexerTokens.CPAREN, HASKELL_PARENTHESIS)

        keys1.put(HaskellLexerTokens.OCURLY, HASKELL_CURLY)
        keys1.put(HaskellLexerTokens.CCURLY, HASKELL_CURLY)

        keys1.put(HaskellLexerTokens.OBRACK, HASKELL_BRACKETS)
        keys1.put(HaskellLexerTokens.CBRACK, HASKELL_BRACKETS)

        keys1.put(HaskellLexerTokens.DCOLON, HASKELL_DOUBLE_COLON)
        keys1.put(HaskellLexerTokens.EQUAL, HASKELL_EQUAL)

        keys1.put(HaskellLexerTokens.AT,         HASKELL_IDENTIFIER)
        keys1.put(HaskellLexerTokens.UNDERSCORE, HASKELL_IDENTIFIER)
        keys1.put(HaskellLexerTokens.VARID,      HASKELL_IDENTIFIER)
        keys1.put(HaskellLexerTokens.QVARID,     HASKELL_IDENTIFIER)

        keys1.put(HaskellLexerTokens.CONID,  HASKELL_CONSTRUCTOR)
        keys1.put(HaskellLexerTokens.QCONID, HASKELL_CONSTRUCTOR)
        keys1.put(HaskellLexerTokens.CONSYM, HASKELL_CONSTRUCTOR)
        keys1.put(HaskellLexerTokens.COLON,  HASKELL_CONSTRUCTOR)

        keys1.put(HaskellLexerTokens.VARSYM, HASKELL_OPERATOR)
        keys1.put(HaskellLexerTokens.MINUS,  HASKELL_OPERATOR)

        keys1.put(HaskellLexerTokens.STRING, HASKELL_STRING_LITERAL)
        keys1.put(HaskellLexerTokens.CHAR,   HASKELL_STRING_LITERAL)

        keys1.put(HaskellLexerTokens.INTEGER, HASKELL_NUMBER)

        keys1.put(HaskellLexerTokens.DARROW, HASKELL_CLASS)
    }



    init {
        DISPLAY_NAMES.put(HASKELL_KEYWORD, Pair<String, HighlightSeverity>("Property value", null))
        DISPLAY_NAMES.put(HASKELL_COMMENT, Pair<String, HighlightSeverity>("Comment", null))
    }

}
