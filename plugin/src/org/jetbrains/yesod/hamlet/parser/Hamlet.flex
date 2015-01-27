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

DOCTYPE_ALL      = "$doctype" | "!!!" | "<!"
COMMENTS         = "<!--#" | "$#"


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
"$elseif"             { return HamletTokenTypes.ELSEIF_DOLLAR; }
{DOCTYPE_ALL}         { return HamletTokenTypes.DOCTYPE_ALL; }
{COMMENTS}            { return HamletTokenTypes.COMMENTS; }
"<!--"                { return HamletTokenTypes.COMMENT_START; }
"-->"                 { return HamletTokenTypes.COMMENT_END; }
"$forall"             { return HamletTokenTypes.FORALL_DOLLAR; }
"$case"               { return HamletTokenTypes.CASE_DOLLAR; }
"$maybe"              { return HamletTokenTypes.MAYBE_DOLLAR; }
"$nothing"            { return HamletTokenTypes.NOTHING_DOLLAR; }
"$of"                 { return HamletTokenTypes.OF_DOLLAR; }
"$with"               { return HamletTokenTypes.WITH_DOLLAR; }
"\\"                  { return HamletTokenTypes.BACKSLASH; }
[A-Za-z0-9_-]+        { return HamletTokenTypes.IDENTIFIER; }
"$"                   { return HamletTokenTypes.DOLLAR; }
.                     { return TokenType.BAD_CHARACTER; }