package org.jetbrains.cabal.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import gnu.trove.THashMap
import org.jetbrains.cabal.parser.CabalLexer
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.haskell.highlight.HaskellHighlighter
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors

public class CabalHighlighter : SyntaxHighlighterBase() {
    class object {
        public val CABAL_STRING_LITERAL: TextAttributesKey = TextAttributesKey.createTextAttributesKey("CABAL_STRING_LITERAL", DefaultLanguageHighlighterColors.STRING)
        public val CABAL_COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("CABAL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        public val CABAL_PROPERTY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("CABAL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        public val CABAL_COLON: TextAttributesKey = TextAttributesKey.createTextAttributesKey("CABAL_COLON", DefaultLanguageHighlighterColors.SEMICOLON)
    }


    override fun getHighlightingLexer(): Lexer {
        return CabalLexer()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return SyntaxHighlighterBase.pack(keys1.get(tokenType))
    }


    private val keys1: MutableMap<IElementType, TextAttributesKey>

    {
        keys1 = THashMap<IElementType, TextAttributesKey>()

        keys1.put(CabalTokelTypes.STRING, CABAL_STRING_LITERAL)
        keys1.put(CabalTokelTypes.END_OF_LINE_COMMENT, CABAL_COMMENT)
        keys1.put(CabalTokelTypes.COMMENT, CABAL_COMMENT)
        keys1.put(CabalTokelTypes.COLON, CABAL_COLON)
        keys1.put(CabalTokelTypes.TAB, HighlighterColors.BAD_CHARACTER)
    }

}