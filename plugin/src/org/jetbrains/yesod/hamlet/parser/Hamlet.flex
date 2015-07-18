package org.jetbrains.yesod.hamlet.parser;

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
%class _HamletLexer
%implements FlexLexer

%{

%}


%function advance
%type IElementType


DIGIT             = [0-9]
WHITE_SPACE_CHAR  = [\ \f\t]
EOL_COMMENT       = {INDENT}"--"[^\n]*

DOCTYPE           = "$doctype" | "!!!" | "<!"
COMMENT           = "<!--#" | "$#"
OPERATOR          = "$if"     |
                    "$else"   |
                    "$elseif" |
                    "$forall" |
                    "$case"   |
                    "$maybe"  |
                    "$nothing"|
                    "$of"     |
                    "$with"
INTERPOLATION     = "*{" | "_{" | "^{" | "@{" | "@?{" | "#{"
ESCAPE            = "\\" | "#"

%%


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
{DOCTYPE}             { return HamletTokenTypes.DOCTYPE; }
{COMMENT}             { return HamletTokenTypes.COMMENT; }
{OPERATOR}            { return HamletTokenTypes.OPERATOR; }
{INTERPOLATION}       { return HamletTokenTypes.INTERPOLATION; }
{ESCAPE}              { return HamletTokenTypes.ESCAPE; }
[A-Za-z0-9_-]+        { return HamletTokenTypes.IDENTIFIER; }
"."[A-Za-z0-9_-]+     { return HamletTokenTypes.DOT_IDENTIFIER; }
":"[A-Za-z0-9_-]+":"  { return HamletTokenTypes.COLON_IDENTIFIER; }
"#"[A-Za-z0-9_-]+     { return HamletTokenTypes.SHARP_IDENTIFIER; }
\"([^\\\"\n]|\\.)*\"  { return HamletTokenTypes.STRING; }
"/"                   { return HamletTokenTypes.SLASH; }
"\n"                  { return HamletTokenTypes.NEWLINE; }
"}"                   { return HamletTokenTypes.END_INTERPOLATION; }
"="                   { return HamletTokenTypes.EQUAL; }
"<"                   { return HamletTokenTypes.OANGLE; }
">"                   { return HamletTokenTypes.CANGLE; }
"<!--"                { return HamletTokenTypes.COMMENT_START; }
"-->"                 { return HamletTokenTypes.COMMENT_END; }
"<-"                  { return HamletTokenTypes.BINDSTATMENT; }
"."                   { return HamletTokenTypes.DOT; }
.                     { return TokenType.BAD_CHARACTER; }