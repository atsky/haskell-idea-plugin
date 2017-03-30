package org.jetbrains.yesod.lucius.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

object LuciusTokenTypes {

    @JvmField
    val IDENTIFIER: IElementType = LuciusToken("identifier")

    @JvmField
    val NEWLINE: IElementType = LuciusToken("newline")

    @JvmField
    val INTERPOLATION: IElementType = LuciusToken("interpolatoin")

    @JvmField
    val FUNCTION: IElementType = LuciusToken("function")

    @JvmField
    val STRING: IElementType = LuciusToken("string")

    @JvmField
    val NUMBER: IElementType = LuciusToken("number")

    @JvmField
    val DOT_IDENTIFIER: IElementType = LuciusToken("dot_indentifier")

    @JvmField
    val SHARP_IDENTIFIER: IElementType = LuciusToken("sharp_indentifier")

    @JvmField
    val AT_IDENTIFIER: IElementType = LuciusToken("at_indentifier")

    @JvmField
    val COLON_IDENTIFIER: IElementType = LuciusToken("colon_indentifier")

    @JvmField
    val CC_IDENTIFIER: IElementType = LuciusToken("cc_indentifier")

    @JvmField
    val HYPERLINK: IElementType = LuciusToken("hyperlink")

    @JvmField
    val COMMENT_START: IElementType = LuciusToken("/*")

    @JvmField
    val COMMENT_END: IElementType = LuciusToken("*/")

    @JvmField
    val END_INTERPOLATION: IElementType = LuciusToken("}")

    @JvmField
    val COLON: IElementType = LuciusToken(":")

    @JvmField
    val ANY: IElementType = LuciusCompositeElementType("ANY")

    @JvmField
    val COMMENT: IElementType = LuciusCompositeElementType("COMMENT")

    @JvmField
    val ATTRIBUTE: IElementType = LuciusCompositeElementType("ATTRIBUTE")

    @JvmField
    val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

}
