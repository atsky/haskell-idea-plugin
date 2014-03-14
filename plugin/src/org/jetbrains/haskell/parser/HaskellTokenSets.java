package org.jetbrains.haskell.parser;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.haskell.parser.lexer.LexerPackage;


public interface HaskellTokenSets {
  TokenSet COMMENTS = TokenSet.create(LexerPackage.getEND_OF_LINE_COMMENT(), LexerPackage.getCOMMENT());
  TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE, TokenType.NEW_LINE_INDENT);
}