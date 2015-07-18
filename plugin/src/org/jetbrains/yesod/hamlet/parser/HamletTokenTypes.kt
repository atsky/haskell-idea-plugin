package org.jetbrains.yesod.hamlet.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

public interface HamletTokenTypes {
    companion object {

        public val COMMENT_END: IElementType = HamletToken("-->")
        public val BINDSTATMENT: IElementType = HamletToken("<-")
        public val COMMENT_START: IElementType = HamletToken("<!--")
        public val SLASH: IElementType = HamletToken("/")
        public val DOT: IElementType = HamletToken(".")
        public val END_INTERPOLATION: IElementType = HamletToken("}")
        public val EQUAL: IElementType = HamletToken("=")
        public val OANGLE: IElementType = HamletToken("<")
        public val CANGLE: IElementType = HamletToken(">")

        public val IDENTIFIER: IElementType = HamletToken("identifier")
        public val DOT_IDENTIFIER: IElementType = HamletToken("dot_identifier")
        public val COLON_IDENTIFIER: IElementType = HamletToken("colon_identifier")
        public val SHARP_IDENTIFIER: IElementType = HamletToken("sharp_identifier")
        public val OPERATOR: IElementType = HamletToken("operator")
        public val STRING: IElementType = HamletToken("string")
        public val ESCAPE: IElementType = HamletToken("escape")
        public val NEWLINE: IElementType = HamletToken("newline")
        public val DOCTYPE: IElementType = HamletToken("doctype")
        public val COMMENT: IElementType = HamletToken("comment")
        public val INTERPOLATION: IElementType = HamletToken("interpolation")

        public val ANY: IElementType = HamletCompositeElementType("ANY")
        public val TAG: IElementType = HamletCompositeElementType("TAG")
        public val ATTRIBUTE: IElementType = HamletCompositeElementType("ATTRIBUTE")
        public val ATTRIBUTE_VALUE: IElementType = HamletCompositeElementType("ATTRIBUTE_VALUE")

        public val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
    }

}
