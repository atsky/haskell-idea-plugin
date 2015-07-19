package org.jetbrains.yesod.julius.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

public interface JuliusTokenTypes {
    companion object {

        public val NEWLINE: IElementType = JuliusToken("newline")
        public val KEYWORD: IElementType = JuliusToken("keyword")
        public val NUMBER: IElementType = JuliusToken("number")
        public val STRING: IElementType = JuliusToken("string")
        public val DOT_IDENTIFIER: IElementType = JuliusToken("dot_identifier")
        public val INTERPOLATION: IElementType = JuliusToken("interpolation")
        public val END_INTERPOLATION: IElementType = JuliusToken("}")
        public val COMMENT: IElementType = JuliusToken("//")
        public val COMMENT_END: IElementType = JuliusToken("*/")
        public val COMMENT_START: IElementType = JuliusToken("/*")

        public val ANY: IElementType = JuliusCompositeElementType("ANY")

        public val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
    }
}