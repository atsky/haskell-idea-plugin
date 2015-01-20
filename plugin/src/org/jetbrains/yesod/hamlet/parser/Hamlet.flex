package org.jetbrains.yesod.hamlet.parser;

import java.util.*;
import com.intellij.lexer.*;
import com.intellij.psi.*;
import org.jetbrains.haskell.parser.token.*;
import com.intellij.psi.tree.IElementType;

%%


%unicode
%class _HamletLexer
%implements FlexLexer

%{

%}


%function advance
%type IElementType


DIGIT            = [0-9]
WHITE_SPACE_CHAR = [\ \f]
INDENT           = [\n]({WHITE_SPACE_CHAR}|[\n])*
EOL_COMMENT      = {INDENT}"--"[^\n]*


%%


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
{INDENT}              { return TokenType.NEW_LINE_INDENT; }            // there cannot be more than one new NEW_LINE_INDENT in row
"."                   { return HamletTokenTypes.DOT; }
"{"                   { return HamletTokenTypes.OCURLY; }
"}"                   { return HamletTokenTypes.CCURLY; }
"@"                   { return HamletTokenTypes.AT; }
"="                   { return HamletTokenTypes.EQUAL; }
"#"                   { return HamletTokenTypes.SHARP; }
"="                   { return HamletTokenTypes.EQUAL; }
"#"                   { return HamletTokenTypes.SHARP; }
"<"                   { return HamletTokenTypes.OANGLE; }
">"                   { return HamletTokenTypes.CANGLE; }
[A-Za-z0-9_-]+        { return HamletTokenTypes.IDENTIFIER; }
.                     { return TokenType.BAD_CHARACTER; }