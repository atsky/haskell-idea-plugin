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
%eof{
  return;
%eof}

%xstate STRING, BLOCK_COMMENT

DIGIT =[0-9]
WHITE_SPACE_CHAR = [\ \t\f]
INDENT = [\n] {WHITE_SPACE_CHAR}*
EOL_COMMENT = "--"[^\n]*
LETTER = [^0-9\"(),\ \n\t\f:\\]
IDENTIFIER_PART = ({DIGIT}|{LETTER})
IDENTIFIER = {IDENTIFIER_PART} +

%%

<BLOCK_COMMENT>([^-]|"-"[^}])+ {return CabalTokelTypes.COMMENT;}
<BLOCK_COMMENT>("-}") {  yybegin(YYINITIAL); return CabalTokelTypes.COMMENT; }


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
{INDENT}              { return TokenType.NEW_LINE_INDENT; }
{EOL_COMMENT}         { return CabalTokelTypes.END_OF_LINE_COMMENT; }
"("                   { return CabalTokelTypes.LEFT_PAREN;}
")"                   { return CabalTokelTypes.RIGHT_PAREN;}
":"                   { return CabalTokelTypes.COLON;}
","                   { return CabalTokelTypes.COMMA; }
"{-"[^#]              { yybegin(BLOCK_COMMENT); return CabalTokelTypes.COMMENT; }
({DIGIT})+            { return CabalTokelTypes.NUMBER; }
\"([^\"]|"\\\"")*\"   { return CabalTokelTypes.STRING;}

{IDENTIFIER}          { return CabalTokelTypes.ID; }
.                     { return TokenType.BAD_CHARACTER; }