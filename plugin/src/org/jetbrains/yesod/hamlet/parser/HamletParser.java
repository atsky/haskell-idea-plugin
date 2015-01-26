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
            } else if(token == HamletTokenTypes.DOCTYPE_ALL) {
                parseDoctype(psiBuilder);
            } else if(token == HamletTokenTypes.IF_DOLLAR) {
                parseIf(psiBuilder);
            } else if(token == HamletTokenTypes.ELSE_DOLLAR) {
                parseElse(psiBuilder);
            } else if(token == HamletTokenTypes.ELSEIF_DOLLAR) {
                parseElseIf(psiBuilder);
            } else if(token == HamletTokenTypes.FORALL_DOLLAR) {
                parseForall(psiBuilder);
            } else if(token == HamletTokenTypes.CASE_DOLLAR) {
                parseCase(psiBuilder);
            } else if(token == HamletTokenTypes.MAYBE_DOLLAR) {
                parseMaybe(psiBuilder);
            } else if(token == HamletTokenTypes.NOTHING_DOLLAR) {
                parseNothing(psiBuilder);
            } else if(token == HamletTokenTypes.OF_DOLLAR) {
                parseOf(psiBuilder);
            } else if(token == HamletTokenTypes.WITH_DOLLAR) {
                parseWith(psiBuilder);
            } else if(token == HamletTokenTypes.COMMENTS) {
                parseCommentInLine(psiBuilder);
            } else if(token == HamletTokenTypes.COMMENT_START) {
                parseCommentWithEnd(psiBuilder);
            }  else if(token == HamletTokenTypes.BACKSLASH) {
                parseBackslash(psiBuilder);
            } else if(token == HamletTokenTypes.DOLLAR) {
                parseInvalidDollar(psiBuilder);
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
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.DOCTYPE);
    }

    public void parseIf(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.IF);
    }

    public void parseElse(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.ELSE);
    }

    public void parseElseIf(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.ELSEIF);
    }

    public void parseForall(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.FORALL);
    }

    public void parseCase(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.CASE);
    }

    public void parseMaybe(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.MAYBE);
    }

    public void parseNothing(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.NOTHING);
    }

    public void parseOf(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.OF);
    }

    public void parseWith(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.WITH);
    }

    public void parseCommentInLine(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseNewLine(psiBuilder);
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
        parseNewLine(psiBuilder);
        tagMarker.done(HamletTokenTypes.BACKSLASH);
    }

    public void parseInvalidDollar(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if (token == HamletTokenTypes.DOLLAR) {
                if(token != HamletTokenTypes.COMMANDS)
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(HamletTokenTypes.INVALID_DOLLAR);
    }

    public void parseNewLine(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if (token == HamletTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
    }

    public void parseAny (PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(HamletTokenTypes.ANY);
    }
}
