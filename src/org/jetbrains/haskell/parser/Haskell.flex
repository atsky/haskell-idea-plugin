package org.jetbrains.haskell.parser;

import java.util.*;
import com.intellij.lexer.*;
import com.intellij.psi.*;
import org.jetbrains.haskell.parser.token.*;
import com.intellij.psi.tree.IElementType;

%%

%unicode
%class _HaskellLexer
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
"\\"                  { return HaskellTokenTypes.LAMBDA; }
(->)|(\u2192)         { return HaskellTokenTypes.ARROW; }
"as"                  { return HaskellTokenTypes.AS_KEYWORD; }
"case"                { return HaskellTokenTypes.CASE_KEYWORD; }
"data"                { return HaskellTokenTypes.DATA_KEYWORD; }
"do"                  { return HaskellTokenTypes.DO_KEYWORD; }
"else"                { return HaskellTokenTypes.ELSE_KEYWORD; }
"if"                  { return HaskellTokenTypes.IF_KEYWORD; }
"in"                  { return HaskellTokenTypes.IN_KEYWORD; }
"instance"            { return HaskellTokenTypes.INSTANCE_KEYWORD; }
"hiding"              { return HaskellTokenTypes.HIDING_KEYWORD; }
"let"                 { return HaskellTokenTypes.LET_KEYWORD; }
"module"              { return HaskellTokenTypes.MODULE_KEYWORD; }
"of"                  { return HaskellTokenTypes.OF_KEYWORD; }
"open"                { return HaskellTokenTypes.OPEN_KEYWORD; }
"import"              { return HaskellTokenTypes.IMPORT_KEYWORD; }
"record"              { return HaskellTokenTypes.RECORD_KEYWORD; }
"postulate"           { return HaskellTokenTypes.POSTULATE_KEYWORD; }
"mutual"              { return HaskellTokenTypes.MUTUAL_KEYWORD; }
"class"               { return HaskellTokenTypes.CLASS_KEYWORD; }
"constructor"         { return HaskellTokenTypes.CONSTRUCTOR_KEYWORD; }
"field"               { return HaskellTokenTypes.FIELD_KEYWORD; }
"public"              { return HaskellTokenTypes.PUBLIC_KEYWORD; }
"using"               { return HaskellTokenTypes.USING_KEYWORD; }
"then"                { return HaskellTokenTypes.THEN_KEYWORD; }
"type"                { return HaskellTokenTypes.TYPE_KEYWORD; }
"renaming"            { return HaskellTokenTypes.RENAMING_KEYWORD; }
"where"               { return HaskellTokenTypes.WHERE_KEYWORD; }
"with"                { return HaskellTokenTypes.WITH_KEYWORD; }
"{-#".*"#-}"          { return HaskellTokenTypes.PRAGMA; }
"{-"[^#]              { yybegin(BLOCK_COMMENT); return HaskellTokenTypes.COMMENT; }
({DIGIT})+            { return HaskellTokenTypes.NUMBER; }
(forall)|(\u2200)     { return HaskellTokenTypes.FORALL; }
\'([^\']|\\\')*\'     { return HaskellTokenTypes.CHARACTER; }
\"([^\"]|\\\")*\"     { return HaskellTokenTypes.STRING;}

[A-Z]{IDENTIFIER_PART}* {return HaskellTokenTypes.TYPE_CONS;}
{IDENTIFIER}          { return HaskellTokenTypes.ID; }
.                     { return TokenType.BAD_CHARACTER; }