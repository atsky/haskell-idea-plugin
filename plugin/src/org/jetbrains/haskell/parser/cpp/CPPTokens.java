package org.jetbrains.haskell.parser.cpp;

import org.jetbrains.haskell.parser.HaskellTokenType;

public interface CPPTokens {
    public static HaskellTokenType IF = new HaskellTokenType("#if");
    public static HaskellTokenType IFDEF = new HaskellTokenType("#ifdef");
    public static HaskellTokenType ENDIF = new HaskellTokenType("#endif");
    public static HaskellTokenType ELSE = new HaskellTokenType("#else");
}
