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
import org.jetbrains.haskell.parser.HaskellToken
import java.awt.*
import org.jetbrains.haskell.parser.token.*


public open class HaskellHighlighter() : SyntaxHighlighterBase() {

    public override fun getHighlightingLexer(): Lexer {
        return HaskellLexer()
    }

    public override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return SyntaxHighlighterBase.pack(keys1.get(tokenType))
    }


    private val keys1: MutableMap<IElementType, TextAttributesKey>

    public val STRING_LITERAL: TextAttributesKey = TextAttributesKey.createTextAttributesKey("STRING_LITERAL", DefaultLanguageHighlighterColors.STRING)
    public val KEYWORD_VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("KEYWORD.VALUE", DefaultLanguageHighlighterColors.KEYWORD)
    public val COMMENT_STYLE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    public val TYPE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TYPE", TextAttributes(Color(30, 150, 0), null, null, null, Font.BOLD))
    public val CONSTRUCTOR: TextAttributesKey = TextAttributesKey.createTextAttributesKey("CONSTRUCTOR", TextAttributes(Color(0, 110, 110), null, null, null, Font.ITALIC))
    public val PROPERTIES_VALID_STRING_ESCAPE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("PROPERTIES.VALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE.getDefaultAttributes())
    public val PROPERTIES_INVALID_STRING_ESCAPE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("PROPERTIES.INVALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE.getDefaultAttributes());

    public val DISPLAY_NAMES: MutableMap<TextAttributesKey, Pair<String, HighlightSeverity>> = THashMap<TextAttributesKey, Pair<String, HighlightSeverity>>(4);

    {
        keys1 = THashMap<IElementType, TextAttributesKey>()
        keys1.put(END_OF_LINE_COMMENT, COMMENT_STYLE)
        keys1.put(BLOCK_COMMENT, COMMENT_STYLE)

        for (keyword in org.jetbrains.haskell.parser.token.KEYWORDS)
        {
            keys1.put(keyword, KEYWORD_VALUE)
        }
        keys1.put(PRAGMA, COMMENT_STYLE)
        keys1.put(TYPE_OR_CONS, CONSTRUCTOR)
        keys1.put(OPERATOR_CONS, CONSTRUCTOR)
        keys1.put(COLON, CONSTRUCTOR)
        keys1.put(STRING, STRING_LITERAL)
        keys1.put(CHARACTER, STRING_LITERAL)
        keys1.put(NUMBER, DefaultLanguageHighlighterColors.NUMBER)
        keys1.put(StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN, PROPERTIES_VALID_STRING_ESCAPE)
        keys1.put(StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN, PROPERTIES_INVALID_STRING_ESCAPE)
        keys1.put(StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN, PROPERTIES_INVALID_STRING_ESCAPE)
    }



    {
        DISPLAY_NAMES.put(KEYWORD_VALUE, Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.property.value"), null))
        DISPLAY_NAMES.put(COMMENT_STYLE, Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.comment"), null))
        DISPLAY_NAMES.put(PROPERTIES_VALID_STRING_ESCAPE, Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.valid.string.escape"), null))
        DISPLAY_NAMES.put(PROPERTIES_INVALID_STRING_ESCAPE, Pair<String, HighlightSeverity>(OptionsBundle.message("options.properties.attribute.descriptor.invalid.string.escape"), HighlightSeverity.WARNING))
    }

}
