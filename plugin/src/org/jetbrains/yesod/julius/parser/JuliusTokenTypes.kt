package org.jetbrains.yesod.julius.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

object JuliusTokenTypes {

    @JvmField
    val NEWLINE: IElementType = JuliusToken("newline")

    @JvmField
    val KEYWORD: IElementType = JuliusToken("keyword")

    @JvmField
    val NUMBER: IElementType = JuliusToken("number")

    @JvmField
    val STRING: IElementType = JuliusToken("string")

    @JvmField
    val DOT_IDENTIFIER: IElementType = JuliusToken("dot_identifier")

    @JvmField
    val INTERPOLATION: IElementType = JuliusToken("interpolation")

    @JvmField
    val END_INTERPOLATION: IElementType = JuliusToken("}")

    @JvmField
    val COMMENT: IElementType = JuliusToken("//")

    @JvmField
    val COMMENT_END: IElementType = JuliusToken("*/")

    @JvmField
    val COMMENT_START: IElementType = JuliusToken("/*")

    @JvmField
    val ANY: IElementType = JuliusCompositeElementType("ANY")

    @JvmField
    val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
}