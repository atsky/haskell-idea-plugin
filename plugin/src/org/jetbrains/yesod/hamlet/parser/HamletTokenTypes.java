package org.jetbrains.yesod.hamlet.parser;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface HamletTokenTypes {

    public static IElementType DOT = new HamletToken(".");
    public static IElementType OCURLY = new HamletToken("{");
    public static IElementType CCURLY = new HamletToken("}");
    public static IElementType AT = new HamletToken("@");
    public static IElementType EQUAL = new HamletToken("=");
    public static IElementType SHARP = new HamletToken(".");
    public static IElementType OANGLE = new HamletToken("<");
    public static IElementType CANGLE = new HamletToken(">");
    public static IElementType IDENTIFIER = new HamletToken("identifier");
    public static IElementType NEWLINE = new HamletToken("newline");

    public static IElementType IF_DOLLAR = new HamletToken("if");
    public static IElementType ELSE_DOLLAR = new HamletToken("else");
    public static IElementType ELSEIF_DOLLAR = new HamletToken("elseif");
    public static IElementType DOCTYPE_ALL = new HamletToken("doctype");
    public static IElementType FORALL_DOLLAR = new HamletToken("forall");
    public static IElementType CASE_DOLLAR = new HamletToken("case");
    public static IElementType MAYBE_DOLLAR = new HamletToken("maybe");
    public static IElementType NOTHING_DOLLAR = new HamletToken("nothing");
    public static IElementType OF_DOLLAR = new HamletToken("of");
    public static IElementType WITH_DOLLAR = new HamletToken("with");
    public static IElementType COMMENTS = new HamletToken("comment");
    public static IElementType COMMENT_END = new HamletToken("-->");
    public static IElementType COMMENT_START = new HamletToken("<!--");
    public static IElementType BACKSLASH = new HamletToken("\\");

    public static TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);

    public static IElementType ANY = new HamletCompositeElementType("ANY");
    public static IElementType TAG = new HamletCompositeElementType("TAG");
    public static IElementType DOCTYPE = new HamletCompositeElementType("DOCTYPE");
    public static IElementType IF = new HamletCompositeElementType("IF");
    public static IElementType ELSE = new HamletCompositeElementType("ELSE");
    public static IElementType ELSEIF = new HamletCompositeElementType("ELSEIF");
    public static IElementType FORALL = new HamletCompositeElementType("FORALL");
    public static IElementType CASE = new HamletCompositeElementType("CASE");
    public static IElementType MAYBE = new HamletCompositeElementType("MAYBE");
    public static IElementType NOTHING = new HamletCompositeElementType("NOTHING");
    public static IElementType OF = new HamletCompositeElementType("OF");
    public static IElementType WITH = new HamletCompositeElementType("WITH");
    public static IElementType COMMENT = new HamletCompositeElementType("COMMENT");

}
