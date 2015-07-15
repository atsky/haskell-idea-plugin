package org.jetbrains.yesod.hamlet.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface HamletTokenTypes {

    IElementType COMMENT_END = new HamletToken("-->");
    IElementType BINDSTATMENT = new HamletToken("<-");
    IElementType COMMENT_START = new HamletToken("<!--");
    IElementType BACKSLASH = new HamletToken("\\");
    IElementType DOLLAR = new HamletToken("$");
    IElementType SLASH = new HamletToken("/");
    IElementType DOT = new HamletToken(".");
    IElementType END_INTERPOLATION = new HamletToken("}");
    IElementType EQUAL = new HamletToken("=");
    IElementType OANGLE = new HamletToken("<");
    IElementType CANGLE = new HamletToken(">");

    IElementType IDENTIFIER = new HamletToken("identifier");
    IElementType DOT_IDENTIFIER = new HamletToken("dot_identifier");
    IElementType COLON_IDENTIFIER = new HamletToken("colon_identifier");
    IElementType SHARP_IDENTIFIER = new HamletToken("sharp_identifier");
    IElementType OPERATOR = new HamletToken("operator");
    IElementType STRING = new HamletToken("string");

    IElementType NEWLINE = new HamletToken("newline");
    IElementType DOCTYPE = new HamletToken("doctype");
    IElementType COMMENT = new HamletToken("comment");
    IElementType INTERPOLATION = new HamletToken("interpolation");

    IElementType ANY = new HamletCompositeElementType("ANY");
    IElementType TAG = new HamletCompositeElementType("TAG");
    IElementType ATTRIBUTE = new HamletCompositeElementType("ATTRIBUTE");
    IElementType ATTRIBUTE_VALUE = new HamletCompositeElementType("ATTRIBUTE_VALUE");

    TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);

}
