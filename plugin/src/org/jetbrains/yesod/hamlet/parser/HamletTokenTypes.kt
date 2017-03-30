package org.jetbrains.yesod.hamlet.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

object HamletTokenTypes {
    @JvmField
    val COMMENT_END: IElementType = HamletToken("-->")

    @JvmField
    val BINDSTATMENT: IElementType = HamletToken("<-")

    @JvmField
    val COMMENT_START: IElementType = HamletToken("<!--")

    @JvmField
    val SLASH: IElementType = HamletToken("/")

    @JvmField
    val DOT: IElementType = HamletToken(".")

    @JvmField
    val END_INTERPOLATION: IElementType = HamletToken("}")

    @JvmField
    val EQUAL: IElementType = HamletToken("=")

    @JvmField
    val OANGLE: IElementType = HamletToken("<")

    @JvmField
    val CANGLE: IElementType = HamletToken(">")

    @JvmField
    val IDENTIFIER: IElementType = HamletToken("identifier")

    @JvmField
    val DOT_IDENTIFIER: IElementType = HamletToken("dot_identifier")

    @JvmField
    val COLON_IDENTIFIER: IElementType = HamletToken("colon_identifier")

    @JvmField
    val SHARP_IDENTIFIER: IElementType = HamletToken("sharp_identifier")

    @JvmField
    val OPERATOR: IElementType = HamletToken("operator")

    @JvmField
    val STRING: IElementType = HamletToken("string")

    @JvmField
    val ESCAPE: IElementType = HamletToken("escape")

    @JvmField
    val NEWLINE: IElementType = HamletToken("newline")

    @JvmField
    val DOCTYPE: IElementType = HamletToken("doctype")

    @JvmField
    val COMMENT: IElementType = HamletToken("comment")

    @JvmField
    val INTERPOLATION: IElementType = HamletToken("interpolation")

    @JvmField
    val ANY: IElementType = HamletCompositeElementType("ANY")

    @JvmField
    val TAG: IElementType = HamletCompositeElementType("TAG")

    @JvmField
    val ATTRIBUTE: IElementType = HamletCompositeElementType("ATTRIBUTE")

    @JvmField
    val ATTRIBUTE_VALUE: IElementType = HamletCompositeElementType("ATTRIBUTE_VALUE")

    @JvmField
    val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

}
