package org.jetbrains.yesod.julius.parser;

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


public class JuliusParser implements PsiParser {
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
            if(token == JuliusTokenTypes.COMMENTS) {
                parseCommentInLine(psiBuilder);
            } else if(token == JuliusTokenTypes.COMMENT_START) {
                parseCommentWithEnd(psiBuilder);
            } else if(token == JuliusTokenTypes.VAR) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.INSTANCEOF) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.FUNCTION) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.IF) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.ELSE) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.CASE) {
                parseCase(psiBuilder);
            } else if(token == JuliusTokenTypes.SWITCH) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.BREAK) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.DEFAULT) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.FOR) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.WHILE) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.DO) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.CONTINUE) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.NEW) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.DELETE) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.RETURN) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.CATCH) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.TRY) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.THROW) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.FINALLY) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.IN) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.TYPEOF) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.WITH) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.THIS) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.DEBUGGER) {
                parseOperator(psiBuilder);
            } else if(token == JuliusTokenTypes.BACKSLASH) {
                parseBackslash(psiBuilder);
            } else if(token == JuliusTokenTypes.UNDERLINE) {
                parseSign(psiBuilder);
            } else if(token == JuliusTokenTypes.DOLLAR) {
                parseSign(psiBuilder);
            } else if(token == JuliusTokenTypes.STRING) {
                parseString(psiBuilder);
            } else if(token == JuliusTokenTypes.AT || token == JuliusTokenTypes.HAT || token == JuliusTokenTypes.SHARP ||
                      token == JuliusTokenTypes.ATWITHQ) {
                parseCurly(psiBuilder);
            } else {
                parseAny(psiBuilder);
            }
        }
    }

    public void parseOperator(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(JuliusTokenTypes.OPERATOR);
    }

    public void parseSign(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(JuliusTokenTypes.SIGN);
    }

    public void parseCommentInLine(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseUntil(psiBuilder);
        tagMarker.done(JuliusTokenTypes.COMMENT);
    }

    public void parseCase(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == JuliusTokenTypes.COLON) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(JuliusTokenTypes.OPERATOR);
    }

    public void parseCommentWithEnd(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == JuliusTokenTypes.COMMENT_END) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(JuliusTokenTypes.COMMENT);
    }

    public void parseBackslash(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        parseUntil(psiBuilder);
        tagMarker.done(JuliusTokenTypes.BACKSLASH);
    }

    public void parseUntil(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if (token == JuliusTokenTypes.NEWLINE) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
    }

    public void parseAny(PsiBuilder psiBuilder) {
        Marker marker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        marker.done(JuliusTokenTypes.ANY);
    }

    public void parseCurly(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        while (!psiBuilder.eof()) {
            IElementType token = psiBuilder.getTokenType();
            if(token == JuliusTokenTypes.CCURLY) {
                psiBuilder.advanceLexer();
                break;
            }
            parseAny(psiBuilder);
        }
        tagMarker.done(JuliusTokenTypes.CURLY);
    }

    public void parseString(PsiBuilder psiBuilder) {
        Marker tagMarker = psiBuilder.mark();
        psiBuilder.advanceLexer();
        tagMarker.done(JuliusTokenTypes.STRING);
    }
}