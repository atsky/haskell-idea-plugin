package org.jetbrains.haskell.parser.cpp;

import org.jetbrains.haskell.parser.HaskellTokenType;

public interface CPPTokens {
    HaskellTokenType IF = new HaskellTokenType("#if");
    HaskellTokenType IFDEF = new HaskellTokenType("#ifdef");
    HaskellTokenType ENDIF = new HaskellTokenType("#endif");
    HaskellTokenType ELSE = new HaskellTokenType("#else");
}
