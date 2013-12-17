package org.jetbrains.haskell.parser;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.haskell.parser.token.HaskellTokenTypes;


public interface HaskellTokenSets {
  TokenSet COMMENTS = TokenSet.create(HaskellTokenTypes.END_OF_LINE_COMMENT, HaskellTokenTypes.COMMENT);
  TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE, TokenType.NEW_LINE_INDENT);


}