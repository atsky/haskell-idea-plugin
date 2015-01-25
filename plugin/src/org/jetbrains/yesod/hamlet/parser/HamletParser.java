package org.jetbrains.yesod.hamlet.parser;

import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.ASTNode;
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
            if(token == HamletTokenTypes.OANGLE) {
                parseTag(psiBuilder);
            } else if(token == HamletTokenTypes.DOCTYPE_DOLLAR) {
                parseDoctype(psiBuilder);
            } else if(token == HamletTokenTypes.IF_DOLLAR) {
                parseIf(psiBuilder);
            } else if(token == HamletTokenTypes.ELSE_DOLLAR) {
                parseElse(psiBuilder);
            } else if(token == HamletTokenTypes.FORALL_DOLLAR) {
                parseForall(psiBuilder);
            } else {
                parseAny(psiBuilder);
            }
        }
    }

    public void parseTag(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == HamletTokenTypes.CANGLE) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(HamletTokenTypes.TAG);
    }

    public void parseDoctype(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == HamletTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(HamletTokenTypes.DOCTYPE);
    }

    public void parseIf(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == HamletTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(HamletTokenTypes.IF);
    }

    public void parseElse(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == HamletTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(HamletTokenTypes.ELSE);
    }

    public void parseForall(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == HamletTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(HamletTokenTypes.FORALL);
    }

    public void parseAny (PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(HamletTokenTypes.ANY);
    }
}
