package org.jetbrains.yesod.julius.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface JuliusTokenTypes {

    IElementType ATWITHQ = new JuliusToken("@?{");
    IElementType CCURLY = new JuliusToken("}");
    IElementType AT = new JuliusToken("@{");
    IElementType SHARP = new JuliusToken("#{");
    IElementType HAT = new JuliusToken("^{");
    IElementType NEWLINE = new JuliusToken("newline");
    IElementType COMMENTS = new JuliusToken("comment");
    IElementType COMMENT_END = new JuliusToken("*/");
    IElementType COMMENT_START = new JuliusToken("/*");
    IElementType BACKSLASH = new JuliusToken("\\");
    IElementType DOLLAR = new JuliusToken("$");
    IElementType UNDERLINE = new JuliusToken("_");
    IElementType COLON = new JuliusToken(":");


    IElementType VAR = new JuliusToken("var");
    IElementType FUNCTION = new JuliusToken("function");
    IElementType INSTANCEOF = new JuliusToken("instanceof");
    IElementType IF = new JuliusToken("if");
    IElementType ELSE = new JuliusToken("else");
    IElementType SWITCH = new JuliusToken("switch");
    IElementType CASE = new JuliusToken("case");
    IElementType BREAK = new JuliusToken("break");
    IElementType DEFAULT = new JuliusToken("default");
    IElementType FOR = new JuliusToken("for");
    IElementType WHILE = new JuliusToken("while");
    IElementType DO = new JuliusToken("do");
    IElementType CONTINUE = new JuliusToken("continue");
    IElementType NEW = new JuliusToken("new");
    IElementType DELETE = new JuliusToken("delete");
    IElementType RETURN = new JuliusToken("return");
    IElementType CATCH = new JuliusToken("catch");
    IElementType TRY = new JuliusToken("try");
    IElementType THROW = new JuliusToken("throw");
    IElementType FINALLY = new JuliusToken("finally");
    IElementType IN = new JuliusToken("in");
    IElementType TYPEOF = new JuliusToken("typeof");
    IElementType WITH = new JuliusToken("with");
    IElementType THIS = new JuliusToken("this");
    IElementType DEBUGGER = new JuliusToken("debugger");

    TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);

    IElementType ANY = new JuliusCompositeElementType("ANY");
    IElementType COMMENT = new JuliusCompositeElementType("COMMENT");
    IElementType STRING = new JuliusCompositeElementType("STRING");
    IElementType CURLY = new JuliusCompositeElementType("CURLY");
    IElementType SIGN = new JuliusCompositeElementType("SIGN");
    IElementType OPERATOR = new JuliusCompositeElementType("OPERATOR");

}