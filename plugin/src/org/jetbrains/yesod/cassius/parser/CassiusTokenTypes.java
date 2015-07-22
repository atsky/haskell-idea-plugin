package org.jetbrains.yesod.cassius.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface CassiusTokenTypes {

    IElementType DOT = new CassiusToken(".");
    IElementType IDENTIFIER = new CassiusToken("identifier");
    IElementType NEWLINE = new CassiusToken("newline");

    TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
}
