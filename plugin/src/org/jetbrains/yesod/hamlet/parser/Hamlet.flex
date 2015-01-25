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
WHITE_SPACE_CHAR = [\ \f\t]
EOL_COMMENT      = {INDENT}"--"[^\n]*


%%


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
"\n"                  { return HamletTokenTypes.NEWLINE; }
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
"$if"                 { return HamletTokenTypes.IF_DOLLAR; }
"$else"               { return HamletTokenTypes.ELSE_DOLLAR; }
"$doctype"            { return HamletTokenTypes.DOCTYPE_DOLLAR; }
"$forall"             { return HamletTokenTypes.FORALL_DOLLAR; }
[A-Za-z0-9_-]+        { return HamletTokenTypes.IDENTIFIER; }
.                     { return TokenType.BAD_CHARACTER; }