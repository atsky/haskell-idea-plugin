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


public class JuliusParser : PsiParser {
    override fun parse(root: IElementType, psiBuilder: PsiBuilder): ASTNode {
        val rootmMarker = psiBuilder.mark()
        parseText(psiBuilder)
        rootmMarker.done(root)
        return psiBuilder.getTreeBuilt()
    }

    public fun parseText(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
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

    public fun parseKeyword(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.KEYWORD)
    }

    public fun parseCommentInLine(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        parseUntil(psiBuilder)
        tagMarker.done(JuliusTokenTypes.COMMENT)
    }

    public fun parseCommentWithEnd(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token == JuliusTokenTypes.COMMENT_END) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
        tagMarker.done(JuliusTokenTypes.COMMENT)
    }

    public fun parseUntil(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token == JuliusTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
    }

    public fun parseAny(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(JuliusTokenTypes.ANY)
    }

    public fun parseString(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.STRING)
    }

    public fun parseNumber(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.NUMBER)
    }

    public fun parseInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.INTERPOLATION)

        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token == JuliusTokenTypes.END_INTERPOLATION) {
                parseEndInterpolation(psiBuilder)
                break
            } else
                parseAny(psiBuilder)
        }
    }

    public fun parseEndInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.END_INTERPOLATION)
    }

    public fun parseDotIdentifier(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(JuliusTokenTypes.DOT_IDENTIFIER)
    }

}