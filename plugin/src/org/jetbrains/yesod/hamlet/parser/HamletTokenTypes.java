package org.jetbrains.yesod.hamlet.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface HamletTokenTypes {

    IElementType DOT = new HamletToken(".");
    IElementType OCURLY = new HamletToken("@?{");
    IElementType CCURLY = new HamletToken("}");
    IElementType AT = new HamletToken("@{");
    IElementType EQUAL = new HamletToken("=");
    IElementType SHARP = new HamletToken("#{");
    IElementType OANGLE = new HamletToken("<");
    IElementType CANGLE = new HamletToken(">");
    IElementType HAT = new HamletToken("^{");
    IElementType UNDERSCORE = new HamletToken("_{");
    IElementType STAR = new HamletToken("*{");
    IElementType IDENTIFIER = new HamletToken("identifier");
    IElementType NEWLINE = new HamletToken("newline");

    IElementType IF_DOLLAR = new HamletToken("if");
    IElementType ELSE_DOLLAR = new HamletToken("else");
    IElementType ELSEIF_DOLLAR = new HamletToken("elseif");
    IElementType DOCTYPE_ALL = new HamletToken("doctype");
    IElementType FORALL_DOLLAR = new HamletToken("forall");
    IElementType CASE_DOLLAR = new HamletToken("case");
    IElementType MAYBE_DOLLAR = new HamletToken("maybe");
    IElementType NOTHING_DOLLAR = new HamletToken("nothing");
    IElementType OF_DOLLAR = new HamletToken("of");
    IElementType WITH_DOLLAR = new HamletToken("with");
    IElementType COMMENTS = new HamletToken("comment");
    IElementType COMMENT_END = new HamletToken("-->");
    IElementType COMMENT_START = new HamletToken("<!--");
    IElementType BINDSTATMENT = new HamletToken("<-");
    IElementType BACKSLASH = new HamletToken("\\");
    IElementType DOLLAR = new HamletToken("$");

    TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);

    IElementType ANY = new HamletCompositeElementType("ANY");
    IElementType TAG = new HamletCompositeElementType("TAG");
    IElementType DOCTYPE = new HamletCompositeElementType("DOCTYPE");
    IElementType IF = new HamletCompositeElementType("IF");
    IElementType ELSE = new HamletCompositeElementType("ELSE");
    IElementType ELSEIF = new HamletCompositeElementType("ELSEIF");
    IElementType FORALL = new HamletCompositeElementType("FORALL");
    IElementType CASE = new HamletCompositeElementType("CASE");
    IElementType MAYBE = new HamletCompositeElementType("MAYBE");
    IElementType NOTHING = new HamletCompositeElementType("NOTHING");
    IElementType OF = new HamletCompositeElementType("OF");
    IElementType WITH = new HamletCompositeElementType("WITH");
    IElementType COMMENT = new HamletCompositeElementType("COMMENT");
    IElementType INVALID_DOLLAR = new HamletCompositeElementType("INVALID_DOLLAR");
    IElementType CURLY = new HamletCompositeElementType("CURLY");
    IElementType SIGN = new HamletCompositeElementType("SIGN");
    IElementType OPERATOR = new HamletCompositeElementType("OPERATOR");

}
