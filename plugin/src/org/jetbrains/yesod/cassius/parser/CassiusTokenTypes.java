package org.jetbrains.yesod.cassius.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface CassiusTokenTypes {

    public static IElementType DOT = new CassiusToken(".");
    public static IElementType IDENTIFIER = new CassiusToken("identifier");
    public static IElementType NEWLINE = new CassiusToken("newline");
    public static TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
}
