package org.jetbrains.yesod.julius.parser

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


class JuliusParser : PsiParser {
    override fun parse(root: IElementType, psiBuilder: PsiBuilder): ASTNode {
        val rootmMarker = psiBuilder.mark()
        parseText(psiBuilder)
        rootmMarker.done(root)
        return psiBuilder.treeBuilt
    }

    fun parseText(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.tokenType
            if (token == JuliusTokenTypes.COMMENT) {
                parseCommentInLine(psiBuilder)
            } else if (token == JuliusTokenTypes.COMMENT_START) {
                parseCommentWithEnd(psiBuilder)
            } else if (token == JuliusTokenTypes.STRING) {
                parseString(psiBuilder)
            } else if (token == JuliusTokenTypes.INTERPOLATION) {
                parseInterpolation(psiBuilder)
            } else if (token == JuliusTokenTypes.DOT_IDENTIFIER) {
                parseDotIdentifier(psiBuilder)
            } else if (token == JuliusTokenTypes.NUMBER) {
                parseNumber(psiBuilder)
            } else if (token == JuliusTokenTypes.KEYWORD) {
                parseKeyword(psiBuilder)
            } else {
                parseAny(psiBuilder)
            }
        }
    }

    fun parseKeyword(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.KEYWORD)
    }

    fun parseCommentInLine(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        parseUntil(psiBuilder)
        tagMarker.done(JuliusTokenTypes.COMMENT)
    }

    fun parseCommentWithEnd(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        while (!psiBuilder.eof()) {
            val token = psiBuilder.tokenType
            if (token == JuliusTokenTypes.COMMENT_END) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
        tagMarker.done(JuliusTokenTypes.COMMENT)
    }

    fun parseUntil(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.tokenType
            if (token == JuliusTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
    }

    fun parseAny(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(JuliusTokenTypes.ANY)
    }

    fun parseString(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.STRING)
    }

    fun parseNumber(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.NUMBER)
    }

    fun parseInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.INTERPOLATION)

        while (!psiBuilder.eof()) {
            val token = psiBuilder.tokenType
            if (token == JuliusTokenTypes.END_INTERPOLATION) {
                parseEndInterpolation(psiBuilder)
                break
            } else
                parseAny(psiBuilder)
        }
    }

    fun parseEndInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.END_INTERPOLATION)
    }

    fun parseDotIdentifier(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.DOT_IDENTIFIER)
    }

}