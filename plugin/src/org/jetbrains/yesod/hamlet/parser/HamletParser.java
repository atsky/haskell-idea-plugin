package org.jetbrains.yesod.hamlet.parser;

/**
 * @author Leyla H
 */

import com.intellij.lang.PsiParser;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;


public class HamletParser implements PsiParser {
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
            if (token == HamletTokenTypes.OANGLE) {
                parseTag(psiBuilder);
            } else if (token == HamletTokenTypes.STRING) {
                parseString(psiBuilder);
            } else if (token == HamletTokenTypes.DOCTYPE) {
                parseDoctype(psiBuilder);
            } else if (token == HamletTokenTypes.OPERATOR) {
                parseOperator(psiBuilder);
            } else if (token == HamletTokenTypes.COMMENT) {
                parseCommentInLine(psiBuilder);
            } else if (token == HamletTokenTypes.COMMENT_START) {
                parseCommentWithEnd(psiBuilder);
            } else if (token == HamletTokenTypes.INTERPOLATION) {
                parseInterpolation(psiBuilder);
            } else if (token == HamletTokenTypes.BACKSLASH) {
                parseBackslash(psiBuilder);
            } else {
                parseAny(psiBuilder);
            }
        }
    }

    public void parseAttributeValue(PsiBuilder psiBuilder) {
            Marker tagMarker = psiBuilder.mark();
            psiBuilder.advanceLexer();
            tagMarker.done(HamletTokenTypes.ATTRIBUTE_VALUE);
    }

    public void parseTag(PsiBuilder psiBuilder) {
        psiBuilder.advanceLexer();
        IElementType tokenType = psiBuilder.getTokenType();

        if(tokenType == HamletTokenTypes.SLASH) {
            Marker tagMarker = psiBuilder.mark();
            psiBuilder.advanceLexer();
            tagMarker.done(HamletTokenTypes.TAG);
        }

        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.TAG);

        while (tokenType != HamletTokenTypes.CANGLE) {
            tokenType = psiBuilder.getTokenType();

            if (tokenType == HamletTokenTypes.DOT_IDENTIFIER) {
                parseDotIdentifier(psiBuilder);
            } else if (tokenType == HamletTokenTypes.IDENTIFIER) {
                parseAttribute(psiBuilder);
            } else if (tokenType == HamletTokenTypes.COLON_IDENTIFIER) {
                parseColonIdentifier(psiBuilder);
            } else if (tokenType == HamletTokenTypes.SHARP_IDENTIFIER) {
                parseSharpIdentifier(psiBuilder);
            } else if (tokenType == HamletTokenTypes.INTERPOLATION) {
                parseInterpolation(psiBuilder);
            } else if (tokenType == HamletTokenTypes.STRING) {
                parseString(psiBuilder);
            } else if (tokenType == HamletTokenTypes.BACKSLASH) {
                parseBackslash(psiBuilder);
            } else {
                parseAny(psiBuilder);
            }
        }
    }

    private void parseAttribute(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();

        if (psiBuilder.getTokenType() == HamletTokenTypes.EQUAL) {
            marker.done(HamletTokenTypes.ATTRIBUTE);

            psiBuilder.advanceLexer();
            IElementType next = psiBuilder.getTokenType();
            if (next == HamletTokenTypes.STRING) {
                parseString(psiBuilder);
            } else if (next == HamletTokenTypes.INTERPOLATION) {
                parseInterpolation(psiBuilder);
            } else {
                parseAttributeValue(psiBuilder);
            }
        } else {
            marker.drop();
        }
    }

    public void parseDoctype(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseUntil(psiBuilder);
        tagMarker.done(HamletTokenTypes.DOCTYPE);
    }

    public void parseInterpolation(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.INTERPOLATION);

        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();

            if(token == HamletTokenTypes.DOLLAR) {
                    parseDollar(psiBuilder);
            } else if(token == HamletTokenTypes.STRING) {
                    parseString(psiBuilder);
            } else if (token == HamletTokenTypes.END_INTERPOLATION) {
                    parseEndInterpolation(psiBuilder);
                    break;
            } else parseAny(psiBuilder);
        }
    }

    public void parseDotIdentifier (PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.DOT_IDENTIFIER);
    }

    public void parseEndInterpolation (PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.END_INTERPOLATION);
    }

    public void parseDollar(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.DOLLAR);
    }

    public void parseSharpIdentifier (PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.SHARP_IDENTIFIER);
    }

    public void parseColonIdentifier (PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.COLON_IDENTIFIER);
    }

    public void parseOperator(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.OPERATOR);
    }

    public void parseString(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(HamletTokenTypes.STRING);
    }

    public void parseCommentInLine(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseUntil(psiBuilder);
        tagMarker.done(HamletTokenTypes.COMMENT);
    }

    public void parseCommentWithEnd(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == HamletTokenTypes.COMMENT_END) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(HamletTokenTypes.COMMENT);
    }

    public void parseBackslash(PsiBuilder psiBuilder) {
            Marker tagMarker = psiBuilder.mark();
            psiBuilder.advanceLexer();
            tagMarker.done(HamletTokenTypes.BACKSLASH);
    }

    public void parseUntil(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if (token == HamletTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
    }

    public void parseAny(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(HamletTokenTypes.ANY);
    }
}
