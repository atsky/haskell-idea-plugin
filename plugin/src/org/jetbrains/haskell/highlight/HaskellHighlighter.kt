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


public open class HaskellHighlighter() : SyntaxHighlighterBase() {

    class object {
        public val STRING_LITERAL: TextAttributesKey                   = TextAttributesKey.createTextAttributesKey("HASKELL_STRING_LITERAL", DefaultLanguageHighlighterColors.STRING)
        public val HASKELL_KEYWORD: TextAttributesKey                  = TextAttributesKey.createTextAttributesKey("HASKELL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        public val COMMENT: TextAttributesKey                          = TextAttributesKey.createTextAttributesKey("HASKELL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        public val HASKELL_PRAGMA: TextAttributesKey                   = TextAttributesKey.createTextAttributesKey("HASKELL_PAGMA", DefaultLanguageHighlighterColors.LINE_COMMENT)
        public val CONSTRUCTOR: TextAttributesKey                      = TextAttributesKey.createTextAttributesKey("HASKELL_CONSTRUCTOR")
        public val HASKELL_OPERATOR: TextAttributesKey                 = TextAttributesKey.createTextAttributesKey("HASKELL_OPERATOR", DefaultLanguageHighlighterColors.IDENTIFIER)
        public val PROPERTIES_VALID_STRING_ESCAPE: TextAttributesKey   = TextAttributesKey.createTextAttributesKey("PROPERTIES.VALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE)
        public val PROPERTIES_INVALID_STRING_ESCAPE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("PROPERTIES.INVALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
    }

    public override fun getHighlightingLexer(): Lexer {
        return HaskellLexer()
    }

    public override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return SyntaxHighlighterBase.pack(keys1.get(tokenType))
    }


    private val keys1: MutableMap<IElementType, TextAttributesKey>



    public val DISPLAY_NAMES: MutableMap<TextAttributesKey, Pair<String, HighlightSeverity>> = THashMap<TextAttributesKey, Pair<String, HighlightSeverity>>(4);

    {
        keys1 = THashMap<IElementType, TextAttributesKey>()
        keys1.put(END_OF_LINE_COMMENT, COMMENT)
        keys1.put(BLOCK_COMMENT, COMMENT)

        for (keyword in org.jetbrains.haskell.parser.token.KEYWORDS)
        {
            keys1.put(keyword, HASKELL_KEYWORD)
        }
        keys1.put(PRAGMA, HASKELL_PRAGMA)
        keys1.put(HaskellLexerTokens.CONID, CONSTRUCTOR)
        keys1.put(HaskellLexerTokens.QCONID, CONSTRUCTOR)
        keys1.put(HaskellLexerTokens.CONSYM, CONSTRUCTOR)
        keys1.put(HaskellLexerTokens.VARSYM, HASKELL_OPERATOR)
        keys1.put(HaskellLexerTokens.COLON, CONSTRUCTOR)
        keys1.put(HaskellLexerTokens.STRING, STRING_LITERAL)
        keys1.put(HaskellLexerTokens.CHAR, STRING_LITERAL)
        keys1.put(HaskellLexerTokens.INTEGER, DefaultLanguageHighlighterColors.NUMBER)
        keys1.put(StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN, PROPERTIES_VALID_STRING_ESCAPE)
        keys1.put(StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN, PROPERTIES_INVALID_STRING_ESCAPE)
        keys1.put(StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN, PROPERTIES_INVALID_STRING_ESCAPE)
    }



    {
        DISPLAY_NAMES.put(HASKELL_KEYWORD, Pair<String, HighlightSeverity>("Property value", null))
        DISPLAY_NAMES.put(COMMENT, Pair<String, HighlightSeverity>("Comment", null))
        DISPLAY_NAMES.put(PROPERTIES_VALID_STRING_ESCAPE, Pair<String, HighlightSeverity>("Valid string escape", null))
        DISPLAY_NAMES.put(PROPERTIES_INVALID_STRING_ESCAPE, Pair("Invalid string escape", HighlightSeverity.WARNING))
    }

}
