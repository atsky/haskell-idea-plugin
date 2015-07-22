package org.jetbrains.yesod.lucius.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface LuciusTokenTypes {

    IElementType IDENTIFIER = new LuciusToken("identifier");
    IElementType NEWLINE = new LuciusToken("newline");
    IElementType INTERPOLATION = new LuciusToken("interpolatoin");
    IElementType FUNCTION = new LuciusToken("function");
    IElementType STRING = new LuciusToken("string");
    IElementType NUMBER = new LuciusToken("number");
    IElementType DOT_IDENTIFIER = new LuciusToken("dot_indentifier");
    IElementType SHARP_IDENTIFIER = new LuciusToken("sharp_indentifier");
    IElementType AT_IDENTIFIER = new LuciusToken("at_indentifier");
    IElementType COLON_IDENTIFIER = new LuciusToken("colon_indentifier");
    IElementType CC_IDENTIFIER = new LuciusToken("cc_indentifier");

    IElementType COMMENT_START = new LuciusToken("/*");
    IElementType COMMENT_END = new LuciusToken("*/");
    IElementType END_INTERPOLATION = new LuciusToken("}");
    IElementType COLON = new LuciusToken(":");

    IElementType ANY = new LuciusCompositeElementType("ANY");
    IElementType COMMENT = new LuciusCompositeElementType("COMMENT");
    IElementType ATTRIBUTE = new LuciusCompositeElementType("ATTRIBUTE");

    TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
}
