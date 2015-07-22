package org.jetbrains.yesod.lucius.parser;

/**
 * @author Leyla H
 */

import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;


public class LuciusParser implements PsiParser {
    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder psiBuilder) {
        Marker rootmMarker = psiBuilder.mark();
        parseText(psiBuilder);
        rootmMarker.done(root);
        return psiBuilder.getTreeBuilt();
    }

    public void parseText(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if (token == LuciusTokenTypes.COMMENT_START) {
                parseCommentWithEnd(psiBuilder);
            } else if (token == LuciusTokenTypes.STRING) {
                parseString(psiBuilder);
            } else if (token == LuciusTokenTypes.INTERPOLATION) {
                parseInterpolation(psiBuilder);
            } else if (token == LuciusTokenTypes.DOT_IDENTIFIER) {
                parseDotIdentifier(psiBuilder);
            } else if (token == LuciusTokenTypes.NUMBER) {
                parseNumber(psiBuilder);
            } else if (token == LuciusTokenTypes.SHARP_IDENTIFIER) {
                parseSharpIdentifier(psiBuilder);
            } else if (token == LuciusTokenTypes.FUNCTION) {
                parseFunction(psiBuilder);
            } else if (token == LuciusTokenTypes.AT_IDENTIFIER) {
                parseAtIdentifier(psiBuilder);
            } else if (token == LuciusTokenTypes.COLON_IDENTIFIER) {
                parseColonIdentifier(psiBuilder);
            } else if (token == LuciusTokenTypes.CC_IDENTIFIER) {
                parseCCIdentifier(psiBuilder);
            } else if (token == LuciusTokenTypes.IDENTIFIER) {
                parseAttribute(psiBuilder);
            } else {
                parseAny(psiBuilder);
            }
        }
    }

    public void parseCommentWithEnd(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if (token == LuciusTokenTypes.COMMENT_END) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(LuciusTokenTypes.COMMENT);
    }

    public void parseAny(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.ANY);
    }

    public void parseString(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.STRING);
    }

    public void parseInterpolation(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(LuciusTokenTypes.INTERPOLATION);

        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if (token == LuciusTokenTypes.END_INTERPOLATION) {
                parseEndInterpolation(psiBuilder);
                break;
            } else
                parseAny(psiBuilder);
        }
    }

    public void parseEndInterpolation(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.END_INTERPOLATION);
    }

    public void parseDotIdentifier(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.DOT_IDENTIFIER);
    }

    public void parseNumber(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.NUMBER);
    }

    public void parseFunction(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.FUNCTION);
    }

    public void parseSharpIdentifier(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.SHARP_IDENTIFIER);
    }

    public void parseColonIdentifier(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.COLON_IDENTIFIER);
    }

    public void parseAtIdentifier(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.AT_IDENTIFIER);
    }

    public void parseCCIdentifier(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(LuciusTokenTypes.CC_IDENTIFIER);
    }

    public void parseAttribute(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        IElementType next = psiBuilder.getTokenType();
        if(next == LuciusTokenTypes.COLON) {
            marker.done(LuciusTokenTypes.ATTRIBUTE);
        }
        else {
            marker.drop();
        }
    }
}