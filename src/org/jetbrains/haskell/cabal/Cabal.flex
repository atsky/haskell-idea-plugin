package org.jetbrains.haskell.cabal;

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
%eof{
  return;
%eof}

%xstate STRING, BLOCK_COMMENT

DIGIT =[0-9]
WHITE_SPACE_CHAR = [\ \t\f]
INDENT = [\n] {WHITE_SPACE_CHAR}*
EOL_COMMENT = "--"[^\n]*
LETTER = [^0-9\"\[\]{}(),.\ \n\t\f;\\]
IDENTIFIER_PART = ({DIGIT}|{LETTER})
IDENTIFIER = {LETTER} {IDENTIFIER_PART} *

%%

<BLOCK_COMMENT>([^-]|"-"[^}])+ {return HaskellTokenTypes.COMMENT;}
<BLOCK_COMMENT>("-}") {  yybegin(YYINITIAL); return HaskellTokenTypes.COMMENT; }


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
{INDENT}              { return TokenType.NEW_LINE_INDENT; }
{EOL_COMMENT}         { return HaskellTokenTypes.END_OF_LINE_COMMENT; }
"{"                   { return HaskellTokenTypes.LEFT_BRACE; }
"}"                   { return HaskellTokenTypes.RIGHT_BRACE; }
"["                   { return HaskellTokenTypes.LEFT_BRACKET; }
"]"                   { return HaskellTokenTypes.RIGHT_BRACKET; }
"("                   { return HaskellTokenTypes.LEFT_PAREN; }
")"                   { return HaskellTokenTypes.RIGHT_PAREN; }
":"                   { return HaskellTokenTypes.COLON;}
";"                   { return HaskellTokenTypes.SEMICOLON;}
"..."                 { return HaskellTokenTypes.THREE_DOTS; }
"."                   { return HaskellTokenTypes.DOT; }
","                   { return HaskellTokenTypes.COMMA; }
"="                   { return HaskellTokenTypes.ASSIGNMENT; }
"|"                   { return HaskellTokenTypes.VERTICAL_BAR;}
"{-"[^#]              { yybegin(BLOCK_COMMENT); return HaskellTokenTypes.COMMENT; }
({DIGIT})+            { return HaskellTokenTypes.NUMBER; }
\"([^\"]|"\\\"")*\"   { return HaskellTokenTypes.STRING;}

{IDENTIFIER}          { return HaskellTokenTypes.ID; }
.                     { return TokenType.BAD_CHARACTER; }