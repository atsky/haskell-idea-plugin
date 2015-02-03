package org.jetbrains.yesod.cassius.parser;

/**
 * @author Leyla H
 */

import java.util.*;
import com.intellij.lexer.*;
import com.intellij.psi.*;
import org.jetbrains.haskell.parser.token.*;
import com.intellij.psi.tree.IElementType;

%%


%unicode
%class _CassiusLexer
%implements FlexLexer

%{

%}


%function advance
%type IElementType


DIGIT            = [0-9]
WHITE_SPACE_CHAR = [\ \f\t]
EOL_COMMENT      = {INDENT}"--"[^\n]*


%%


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
"\n"                  { return CassiusTokenTypes.NEWLINE; }
"."                   { return CassiusTokenTypes.DOT; }
[A-Za-z0-9_-]+        { return CassiusTokenTypes.IDENTIFIER; }

