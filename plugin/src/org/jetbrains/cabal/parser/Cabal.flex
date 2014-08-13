package org.jetbrains.cabal.parser;

import java.util.*;
import com.intellij.lexer.*;
import com.intellij.psi.*;
import org.jetbrains.haskell.parser.token.*;
import com.intellij.psi.tree.IElementType;

%%


%unicode
%class _CabalLexer
%implements FlexLexer

%{

%}


%function advance
%type IElementType

//%xstate STRING, BLOCK_COMMENT
%xstate BLOCK_COMMENT

DIGIT            = [0-9]
WHITE_SPACE_CHAR = [\ \f]
INDENT           = [\n]({WHITE_SPACE_CHAR}|[\n])*
EOL_COMMENT      = {INDENT}"--"[^\n]*
COMPARATOR       = >= | <= | < | > | ==
LOGIC            = && | \|\|

SIMPLE_LETTER    = [^0-9\"(),\ \n\t\f:\\><={}&|/!]

IDENTIFIER_PART  = {DIGIT} | {SIMPLE_LETTER}
IDENTIFIER       = {IDENTIFIER_PART}+

%%

<BLOCK_COMMENT>([^-]|"-"[^}])+   { return CabalTokelTypes.COMMENT; }
<BLOCK_COMMENT>("-}")            { yybegin(YYINITIAL); return CabalTokelTypes.COMMENT; }


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
{INDENT}              { return TokenType.NEW_LINE_INDENT; }            // there cannot be more than one new NEW_LINE_INDENT in row
{EOL_COMMENT}         { return CabalTokelTypes.END_OF_LINE_COMMENT; }
{COMPARATOR}          { return CabalTokelTypes.COMPARATOR; }
{LOGIC}               { return CabalTokelTypes.LOGIC; }
"("                   { return CabalTokelTypes.OPEN_PAREN; }
")"                   { return CabalTokelTypes.CLOSE_PAREN; }
"!"                   { return CabalTokelTypes.NEGATION; }
"/"                   { return CabalTokelTypes.SLASH; }
":"                   { return CabalTokelTypes.COLON; }
","                   { return CabalTokelTypes.COMMA; }
"="                   { return CabalTokelTypes.EQ; }
"&"                   { return CabalTokelTypes.AND; }
"|"                   { return CabalTokelTypes.OR; }
"{"                   { return CabalTokelTypes.OPEN_CURLY; }
"}"                   { return CabalTokelTypes.CLOSE_CURLY; }
"{-"/[^#]             { yybegin(BLOCK_COMMENT); return CabalTokelTypes.COMMENT; }
({DIGIT})+            { return CabalTokelTypes.NUMBER; }
\"([^\\\"]|\\.)*\"      { return CabalTokelTypes.STRING; }

{IDENTIFIER}          { return CabalTokelTypes.ID; }

\t                    { return CabalTokelTypes.TAB; }
.                     { return TokenType.BAD_CHARACTER; }