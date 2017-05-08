package org.jetbrains.yesod.hamlet.parser

/**
 * @author Leyla H
 */

import com.intellij.lang.PsiParser
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet


class HamletParser : PsiParser {
    override fun parse(root: IElementType, psiBuilder: PsiBuilder): ASTNode {
        val rootmMarker = psiBuilder.mark()
        parseText(psiBuilder)
        rootmMarker.done(root)
        return psiBuilder.treeBuilt
    }

    fun parseText(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.tokenType
            if (token == HamletTokenTypes.OANGLE) {
                parseTag(psiBuilder)
            } else if (token == HamletTokenTypes.STRING) {
                parseString(psiBuilder)
            } else if (token == HamletTokenTypes.DOCTYPE) {
                parseDoctype(psiBuilder)
            } else if (token == HamletTokenTypes.OPERATOR) {
                parseOperator(psiBuilder)
            } else if (token == HamletTokenTypes.COMMENT) {
                parseCommentInLine(psiBuilder)
            } else if (token == HamletTokenTypes.COMMENT_START) {
                parseCommentWithEnd(psiBuilder)
            } else if (token == HamletTokenTypes.INTERPOLATION) {
                parseInterpolation(psiBuilder)
            } else if (token == HamletTokenTypes.ESCAPE) {
                parseEscape(psiBuilder)
            } else {
                parseAny(psiBuilder)
            }
        }
    }

    fun parseAttributeValue(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.ATTRIBUTE_VALUE)
    }

    fun parseTag(psiBuilder: PsiBuilder) {
        psiBuilder.advanceLexer()
        var tokenType = psiBuilder.tokenType

        if (tokenType == HamletTokenTypes.SLASH) {
            val tagMarker = psiBuilder.mark()
            psiBuilder.advanceLexer()
            tagMarker.done(HamletTokenTypes.TAG)
        }

        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.TAG)

        tokenType = psiBuilder.tokenType

        while (tokenType != HamletTokenTypes.CANGLE) {
            if (tokenType == HamletTokenTypes.DOT_IDENTIFIER) {
                parseDotIdentifier(psiBuilder)
            } else if (tokenType == HamletTokenTypes.IDENTIFIER) {
                parseAttribute(psiBuilder)
            } else if (tokenType == HamletTokenTypes.EQUAL) {
                parseEqual(psiBuilder)
            } else if (tokenType == HamletTokenTypes.COLON_IDENTIFIER) {
                parseColonIdentifier(psiBuilder)
            } else if (tokenType == HamletTokenTypes.SHARP_IDENTIFIER) {
                parseSharpIdentifier(psiBuilder)
            } else if (tokenType == HamletTokenTypes.INTERPOLATION) {
                parseInterpolation(psiBuilder)
            } else if (tokenType == HamletTokenTypes.STRING) {
                parseString(psiBuilder)
            } else if (tokenType == HamletTokenTypes.ESCAPE) {
                parseEscape(psiBuilder)
            } else {
                parseAny(psiBuilder)
            }
            tokenType = psiBuilder.tokenType
        }
    }

    fun parseEqual(psiBuilder: PsiBuilder) {
        psiBuilder.advanceLexer()
        val next = psiBuilder.tokenType
        if (next == HamletTokenTypes.STRING) {
            parseString(psiBuilder)
        } else if (next == HamletTokenTypes.INTERPOLATION) {
            parseInterpolation(psiBuilder)
        } else if (next == HamletTokenTypes.CANGLE) {
            return
        } else {
            parseAttributeValue(psiBuilder)
        }
    }


    fun parseAttribute(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(HamletTokenTypes.ATTRIBUTE)

    }

    fun parseDoctype(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        parseUntil(psiBuilder)
        tagMarker.done(HamletTokenTypes.DOCTYPE)
    }

    fun parseInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.INTERPOLATION)

        while (!psiBuilder.eof()) {
            val token = psiBuilder.tokenType
            if (token == HamletTokenTypes.END_INTERPOLATION) {
                parseEndInterpolation(psiBuilder)
                break
            } else
                parseAny(psiBuilder)
        }
    }

    fun parseDotIdentifier(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.DOT_IDENTIFIER)
    }

    fun parseEndInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.END_INTERPOLATION)
    }

    fun parseSharpIdentifier(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.SHARP_IDENTIFIER)
    }

    fun parseColonIdentifier(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.COLON_IDENTIFIER)
    }

    fun parseOperator(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.OPERATOR)
    }

    fun parseString(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.STRING)
    }

    fun parseCommentInLine(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        parseUntil(psiBuilder)
        tagMarker.done(HamletTokenTypes.COMMENT)
    }

    fun parseCommentWithEnd(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        while (!psiBuilder.eof()) {
            val token = psiBuilder.tokenType
            if (token == HamletTokenTypes.COMMENT_END) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
        tagMarker.done(HamletTokenTypes.COMMENT)
    }

    fun parseEscape(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.ESCAPE)
    }

    fun parseUntil(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.tokenType
            if (token == HamletTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
    }

    fun parseAny(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(HamletTokenTypes.ANY)
    }

}
