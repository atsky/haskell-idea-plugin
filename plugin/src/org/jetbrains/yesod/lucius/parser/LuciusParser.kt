package org.jetbrains.yesod.lucius.parser

/**
 * @author Leyla H
 */

import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import com.intellij.lang.ASTNode


public class LuciusParser : PsiParser {
    override fun parse(root: IElementType, psiBuilder: PsiBuilder): ASTNode {
        val rootmMarker = psiBuilder.mark()
        parseText(psiBuilder)
        rootmMarker.done(root)
        return psiBuilder.getTreeBuilt()
    }

    public fun parseText(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token === LuciusTokenTypes.COMMENT_START) {
                parseCommentWithEnd(psiBuilder)
            } else if (token === LuciusTokenTypes.STRING) {
                parseString(psiBuilder)
            } else if (token === LuciusTokenTypes.INTERPOLATION) {
                parseInterpolation(psiBuilder)
            } else if (token === LuciusTokenTypes.DOT_IDENTIFIER) {
                parseDotIdentifier(psiBuilder)
            } else if (token === LuciusTokenTypes.NUMBER) {
                parseNumber(psiBuilder)
            } else if (token === LuciusTokenTypes.SHARP_IDENTIFIER) {
                parseSharpIdentifier(psiBuilder)
            } else if (token === LuciusTokenTypes.FUNCTION) {
                parseFunction(psiBuilder)
            } else if (token === LuciusTokenTypes.AT_IDENTIFIER) {
                parseAtIdentifier(psiBuilder)
            } else if (token === LuciusTokenTypes.COLON_IDENTIFIER) {
                parseColonIdentifier(psiBuilder)
            } else if (token === LuciusTokenTypes.CC_IDENTIFIER) {
                parseCCIdentifier(psiBuilder)
            } else if (token === LuciusTokenTypes.IDENTIFIER) {
                parseAttribute(psiBuilder)
            } else if (token === LuciusTokenTypes.HYPERLINK) {
                parseHyperlink(psiBuilder)
            } else {
                parseAny(psiBuilder)
            }
        }
    }

    public fun parseCommentWithEnd(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token === LuciusTokenTypes.COMMENT_END) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
        tagMarker.done(LuciusTokenTypes.COMMENT)
    }

    public fun parseAny(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.ANY)
    }

    public fun parseHyperlink(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.HYPERLINK)
    }

    public fun parseString(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.STRING)
    }

    public fun parseInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(LuciusTokenTypes.INTERPOLATION)

        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token === LuciusTokenTypes.END_INTERPOLATION) {
                parseEndInterpolation(psiBuilder)
                break
            } else
                parseAny(psiBuilder)
        }
    }

    public fun parseEndInterpolation(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.END_INTERPOLATION)
    }

    public fun parseDotIdentifier(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.DOT_IDENTIFIER)
    }

    public fun parseNumber(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.NUMBER)
    }

    public fun parseFunction(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.FUNCTION)
    }

    public fun parseSharpIdentifier(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.SHARP_IDENTIFIER)
    }

    public fun parseColonIdentifier(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.COLON_IDENTIFIER)
    }

    public fun parseAtIdentifier(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.AT_IDENTIFIER)
    }

    public fun parseCCIdentifier(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(LuciusTokenTypes.CC_IDENTIFIER)
    }

    public fun parseAttribute(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        val next = psiBuilder.getTokenType()
        if (next === LuciusTokenTypes.COLON) {
            marker.done(LuciusTokenTypes.ATTRIBUTE)
        } else {
            marker.drop()
        }
    }
}