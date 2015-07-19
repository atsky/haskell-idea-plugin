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


public class HamletParser : PsiParser {
    override fun parse(root: IElementType, psiBuilder: PsiBuilder): ASTNode {
        val rootmMarker = psiBuilder.mark()
        parseText(psiBuilder)
        rootmMarker.done(root)
        return psiBuilder.getTreeBuilt()
    }

    public fun parseText(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
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

    public fun parseAttributeValue(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.ATTRIBUTE_VALUE)
    }

    public fun parseTag(psiBuilder: PsiBuilder) {
        psiBuilder.advanceLexer()
        var tokenType = psiBuilder.getTokenType()

        if (tokenType == HamletTokenTypes.SLASH) {
            val tagMarker = psiBuilder.mark()
            psiBuilder.advanceLexer()
            tagMarker.done(HamletTokenTypes.TAG)
        }

        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.TAG)

        tokenType = psiBuilder.getTokenType()

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
            tokenType = psiBuilder.getTokenType()
        }
    }

    public fun parseEqual(psiBuilder: PsiBuilder) {
        psiBuilder.advanceLexer()
        val next = psiBuilder.getTokenType()
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


    public fun parseAttribute(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(HamletTokenTypes.ATTRIBUTE)

    }

    public fun parseDoctype(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        parseUntil(psiBuilder)
        tagMarker.done(HamletTokenTypes.DOCTYPE)
    }

    public fun parseInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.INTERPOLATION)

        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token == HamletTokenTypes.END_INTERPOLATION) {
                parseEndInterpolation(psiBuilder)
                break
            } else
                parseAny(psiBuilder)
        }
    }

    public fun parseDotIdentifier(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.DOT_IDENTIFIER)
    }

    public fun parseEndInterpolation(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.END_INTERPOLATION)
    }

    public fun parseSharpIdentifier(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.SHARP_IDENTIFIER)
    }

    public fun parseColonIdentifier(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.COLON_IDENTIFIER)
    }

    public fun parseOperator(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.OPERATOR)
    }

    public fun parseString(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.STRING)
    }

    public fun parseCommentInLine(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        parseUntil(psiBuilder)
        tagMarker.done(HamletTokenTypes.COMMENT)
    }

    public fun parseCommentWithEnd(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token == HamletTokenTypes.COMMENT_END) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
        tagMarker.done(HamletTokenTypes.COMMENT)
    }

    public fun parseEscape(psiBuilder: PsiBuilder) {
        val tagMarker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        tagMarker.done(HamletTokenTypes.ESCAPE)
    }

    public fun parseUntil(psiBuilder: PsiBuilder) {
        while (!psiBuilder.eof()) {
            val token = psiBuilder.getTokenType()
            if (token == HamletTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer()
                break
            }
            parseAny(psiBuilder)
        }
    }

    public fun parseAny(psiBuilder: PsiBuilder) {
        val marker = psiBuilder.mark()
        psiBuilder.advanceLexer()
        marker.done(HamletTokenTypes.ANY)
    }

}
