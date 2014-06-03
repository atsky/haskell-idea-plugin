package org.jetbrains.haskell.parser.lexer;

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


%xstate BLOCK_COMMENT, TEX

unispace    = \x05
white_no_nl = [\ \n\r\f]|{unispace}
whitechar   = {white_no_nl}|[\n]
//tab         = \t

ascdigit  = [0-9]
unidigit  = \x03
decdigit  = {ascdigit}
digit     = {ascdigit}|{unidigit}

//special   = [\(\)\,\;\[\]\`\{\}]
ascsymbol = [\!\#\$\%\&\*\+\.\/\<\=\>\?\@\\\^\|\-\~]
symbol    = {ascsymbol}|{unisymbol}

large     = [A-Z]

ascsmall  = [a-z]
small     = {ascsmall}|"_"

graphic   = {small}|{large}|{symbol}|{digit}|{special}|[\:\"\']

//octit     = [0-7]
//hexit     = [$decdigit A-F a-f]
//symchar   = [$symbol \:]
//nl        = [\n\r]
idchar      = {small}|{large}|{digit}|[\']

//pragmachar = [$small $large $digit]

docsym      = [\| \^ \* \$]


DIGIT =[0-9]
WHITE_SPACE_CHAR = [\ \t\f]
INDENT = [\n] {WHITE_SPACE_CHAR}*
EOL_COMMENT = "--"[^\n]*

%%

<TEX> {
    [^\\]+            { return TokenPackage.getCOMMENT(); }
    "\\begin{code}"   { yybegin(YYINITIAL); return TokenPackage.getCOMMENT(); }
    \\+*              { return TokenPackage.getCOMMENT(); }

}


<BLOCK_COMMENT>([^-]|"-"[^}])+ {return TokenPackage.getCOMMENT();}
<BLOCK_COMMENT>("-}") {  yybegin(YYINITIAL); return TokenPackage.getCOMMENT(); }


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
{INDENT}              { return TokenType.NEW_LINE_INDENT; }
{EOL_COMMENT}         { return TokenPackage.getEND_OF_LINE_COMMENT(); }
"{"                   { return TokenPackage.getLEFT_BRACE(); }
"}"                   { return TokenPackage.getRIGHT_BRACE(); }
"["                   { return TokenPackage.getLEFT_BRACKET(); }
"]"                   { return TokenPackage.getRIGHT_BRACKET(); }
"("                   { return TokenPackage.getLEFT_PAREN(); }
")"                   { return TokenPackage.getRIGHT_PAREN(); }
":"                   { return TokenPackage.getCOLON();}
"::"                  { return TokenPackage.getDOUBLE_COLON(); }
";"                   { return TokenPackage.getSEMICOLON();}
"."                   { return TokenPackage.getDOT(); }
".."                  { return TokenPackage.getDOT_DOT(); }
"$"                   { return TokenPackage.getDOLLAR(); }
","                   { return TokenPackage.getCOMMA(); }
"="                   { return TokenPackage.getEQUALS(); }
"|"                   { return TokenPackage.getVERTICAL_BAR();}
"\\"                  { return TokenPackage.getBACK_SLASH(); }
"<-"                  { return TokenPackage.getLEFT_ARROW(); }
(->)|(\u2192)         { return TokenPackage.getRIGHT_ARROW(); }

"@"                   { return TokenPackage.getAT(); }
"~"                   { return TokenPackage.getTILDE(); }
"=>"                  { return TokenPackage.getDOUBLE_ARROW(); }
"!"                   { return TokenPackage.getEXCLAMATION(); }
"_"                   { return TokenPackage.getUNDERSCORE(); }

// - Keywords

"as"                  { return TokenPackage.getAS_KW(); }
"case"                { return TokenPackage.getCASE_KW(); }
"class"               { return TokenPackage.getCLASS_KW(); }
"data"                { return TokenPackage.getDATA_KW(); }
"default"             { return TokenPackage.getDEFAULT_KW(); }
"deriving"            { return TokenPackage.getDERIVING_KW(); }
"do"                  { return TokenPackage.getDO_KW(); }
"else"                { return TokenPackage.getELSE_KW(); }
"export"              { return TokenPackage.getEXPORT(); }
"hiding"              { return TokenPackage.getHIDING_KW(); }
"if"                  { return TokenPackage.getIF_KW(); }
"import"              { return TokenPackage.getIMPORT_KW(); }
"in"                  { return TokenPackage.getIN_KW(); }
"infix"               { return TokenPackage.getINFIX_KW(); }
"infixl"              { return TokenPackage.getINFIXL_KW(); }
"infixr"              { return TokenPackage.getINFIXR_KW(); }
"instance"            { return TokenPackage.getINSTANCE_KW(); }
(forall)|(\u2200)     { return TokenPackage.getFORALL_KW(); }
"foreign"             { return TokenPackage.getFOREIGN_KW(); }
"let"                 { return TokenPackage.getLET_KW(); }
"module"              { return TokenPackage.getMODULE_KW(); }
"newtype"             { return TokenPackage.getNEWTYPE_KW(); }
"of"                  { return TokenPackage.getOF_KW(); }
"then"                { return TokenPackage.getTHEN_KW(); }
"qualified"           { return TokenPackage.getQUALIFIED_KW(); }
"safe"                { return TokenPackage.getSAFE(); }
"type"                { return TokenPackage.getTYPE_KW(); }
"unsafe"              { return TokenPackage.getUNSAFE(); }
"where"               { return TokenPackage.getWHERE_KW(); }
"{-#".*"#-}"          { return TokenPackage.getPRAGMA(); }
"{-"[^#]              { yybegin(BLOCK_COMMENT); return TokenPackage.getCOMMENT(); }
({DIGIT})+            { return TokenPackage.getNUMBER(); }
\'([^']|\\.)\'        { return TokenPackage.getCHARACTER(); }
\"([^\"]|\\\")*\"     { return TokenPackage.getSTRING();}
"\\end{code}"         { yybegin(TEX); return TokenPackage.getCOMMENT(); }

{large}{idchar}*      { return TokenPackage.getTYPE_OR_CONS();}
{small}{idchar}*      { return TokenPackage.getID(); }
.                     { return TokenType.BAD_CHARACTER; }