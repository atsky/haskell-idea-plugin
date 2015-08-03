package org.jetbrains.yesod.lucius.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

public interface LuciusTokenTypes {
    companion object {

        public val IDENTIFIER: IElementType = LuciusToken("identifier")
        public val NEWLINE: IElementType = LuciusToken("newline")
        public val INTERPOLATION: IElementType = LuciusToken("interpolatoin")
        public val FUNCTION: IElementType = LuciusToken("function")
        public val STRING: IElementType = LuciusToken("string")
        public val NUMBER: IElementType = LuciusToken("number")
        public val DOT_IDENTIFIER: IElementType = LuciusToken("dot_indentifier")
        public val SHARP_IDENTIFIER: IElementType = LuciusToken("sharp_indentifier")
        public val AT_IDENTIFIER: IElementType = LuciusToken("at_indentifier")
        public val COLON_IDENTIFIER: IElementType = LuciusToken("colon_indentifier")
        public val CC_IDENTIFIER: IElementType = LuciusToken("cc_indentifier")
        public val HYPERLINK: IElementType = LuciusToken("hyperlink")

        public val COMMENT_START: IElementType = LuciusToken("/*")
        public val COMMENT_END: IElementType = LuciusToken("*/")
        public val END_INTERPOLATION: IElementType = LuciusToken("}")
        public val COLON: IElementType = LuciusToken(":")

        public val ANY: IElementType = LuciusCompositeElementType("ANY")
        public val COMMENT: IElementType = LuciusCompositeElementType("COMMENT")
        public val ATTRIBUTE: IElementType = LuciusCompositeElementType("ATTRIBUTE")

        public val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
    }
}
