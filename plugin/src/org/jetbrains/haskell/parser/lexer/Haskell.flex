package org.jetbrains.haskell.parser.lexer;

import java.util.*;
import com.intellij.lexer.*;
import com.intellij.psi.*;
import org.jetbrains.haskell.parser.token.*;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.grammar.HaskellLexerTokens;

%%

%unicode
%class _HaskellLexer
%implements FlexLexer

%{
    private int commentStart;
    private int commentDepth;
%}


%function advance
%type IElementType


%xstate BLOCK_COMMENT, TEX

unispace    = \x05
white_no_nl = [\ \r\t\f]|{unispace}
whitechar   = {white_no_nl}|[\n]
tab         = \t

ascdigit  = [0-9]
unidigit  = \x03
decdigit  = {ascdigit}
digit     = {ascdigit}|{unidigit}

special   = [\(\)\,\;\[\]\`\{\}]
ascsymbol = [\!\#\$\%\&\*\+\.\/\<\=\>\?\@\\\^\|\-\~]
unisymbol = [[\p{P}\p{S}]&&[^(),;\[\]`{}_\"\']]
symbol    = {ascsymbol}|{unisymbol}

large     = [:uppercase:]
ascsmall  = [:lowercase:]
ascLarge  =	[A-Z]
small     = {ascsmall}|"_"

graphic   = {small}|{large}|{symbol}|{digit}|{special}|[\:\"\']

octit     = [0-7]
hexit     = {decdigit}|[A-Fa-f]
symchar   = {symbol}|[\:]
nl        = [\n\r]
idchar      = {small}|{large}|{digit}|[\']

pragmachar = [$small $large $digit]

docsym      = [\| \^ \* \$]



//----- Strings --------

gap        = \\{whitechar}*\\
cntrl      = {ascLarge} | [@\[\\\]\^_]
charesc    = [abfnrtv\\\"\'&]
ascii      = ("^"{cntrl})|(NUL)|(SOH)|(STX)|(ETX)|(EOT)|(ENQ)|(ACK)|(BEL)|(BS)|(HT)|(LF)|(VT)|(FF)|(CR)|(SO)|(SI)|(DLE)|(DC1)|(DC2)|(DC3)|(DC4)|(NAK)|(SYN)|(ETB)|(CAN)|(EM)|(SUB)|(ESC)|(FS)|(GS)|(RS)|(US)|(SP)|(DEL)
escape     = \\({charesc}|{ascii}|({decdigit}+)|(o({octit}+))|(x({hexit}+)))

character  = (\'([^\'\\\n]|{escape})\')
string     = \"([^\"\\\n]|{escape}|{gap})*(\"|\n)

//----- Indent -------

EOL_COMMENT = "--"[^\n]*



%%

<TEX> {
    [^\\]+            { return TokenPackage.getBLOCK_COMMENT(); }
    "\\begin{code}"   { yybegin(YYINITIAL); return TokenPackage.getBLOCK_COMMENT(); }
    \\+*              { return TokenPackage.getBLOCK_COMMENT(); }

}


<BLOCK_COMMENT> {
    "{-" {
         commentDepth++;
    }

    <<EOF>> {
        int state = yystate();
        yybegin(YYINITIAL);
        zzStartRead = commentStart;
        return TokenPackage.getBLOCK_COMMENT();
    }

    "-}" {
        if (commentDepth > 0) {
            commentDepth--;
        }
        else {
             int state = yystate();
             yybegin(YYINITIAL);
             zzStartRead = commentStart;
             return TokenPackage.getBLOCK_COMMENT();
        }
    }

    .|{whitechar} {}
}

"{-" {
    yybegin(BLOCK_COMMENT);
    commentDepth = 0;
    commentStart = getTokenStart();
}


{white_no_nl}+        { return TokenType.WHITE_SPACE; }
"\n"                  { return TokenPackage.getNEW_LINE(); }
{EOL_COMMENT}         { return TokenPackage.getEND_OF_LINE_COMMENT(); }
"{"                   { return HaskellLexerTokens.OCURLY; }
"}"                   { return HaskellLexerTokens.CCURLY; }
"["                   { return HaskellLexerTokens.OBRACK; }
"]"                   { return HaskellLexerTokens.CBRACK; }
"("                   { return HaskellLexerTokens.OPAREN; }
")"                   { return HaskellLexerTokens.CPAREN; }
":"                   { return HaskellLexerTokens.COLON;}
"::"                  { return HaskellLexerTokens.DCOLON; }
";"                   { return HaskellLexerTokens.SEMI;}
"."                   { return HaskellLexerTokens.DOT; }
".."                  { return HaskellLexerTokens.DOTDOT; }
","                   { return HaskellLexerTokens.COMMA; }
"="                   { return HaskellLexerTokens.EQUAL; }
"|"                   { return HaskellLexerTokens.VBAR;}
"\\"                  { return HaskellLexerTokens.LAM; }
"<-"                  { return HaskellLexerTokens.LARROW; }
(->)|(\u2192)         { return HaskellLexerTokens.RARROW; }

"@"                   { return HaskellLexerTokens.AT; }
"~"                   { return HaskellLexerTokens.TILDE; }
"`"                   { return HaskellLexerTokens.BACKQUOTE; }
"=>"                  { return HaskellLexerTokens.DARROW; }
"!"                   { return HaskellLexerTokens.BANG; }
"_"                   { return HaskellLexerTokens.UNDERSCORE; }
{symbol}+             { return HaskellLexerTokens.VARSYM; }
":"{symbol}+          { return HaskellLexerTokens.CONSYM; }

// - Keywords

"as"                  { return HaskellLexerTokens.AS; }
"case"                { return HaskellLexerTokens.CASE; }
"class"               { return HaskellLexerTokens.CLASS; }
"data"                { return HaskellLexerTokens.DATA; }
"default"             { return HaskellLexerTokens.DEFAULT; }
"deriving"            { return HaskellLexerTokens.DERIVING; }
"do"                  { return HaskellLexerTokens.DO; }
"else"                { return HaskellLexerTokens.ELSE; }
"export"              { return HaskellLexerTokens.EXPORT; }
"hiding"              { return HaskellLexerTokens.HIDING; }
"if"                  { return HaskellLexerTokens.IF; }
"import"              { return HaskellLexerTokens.IMPORT; }
"in"                  { return HaskellLexerTokens.IN; }
"infix"               { return HaskellLexerTokens.INFIX; }
"infixl"              { return HaskellLexerTokens.INFIXL; }
"infixr"              { return HaskellLexerTokens.INFIXR; }
"instance"            { return HaskellLexerTokens.INSTANCE; }
("forall")|(\u2200)   { return HaskellLexerTokens.FORALL; }
"foreign"             { return HaskellLexerTokens.FOREIGN; }
"let"                 { return HaskellLexerTokens.LET; }
"module"              { return HaskellLexerTokens.MODULE; }
"newtype"             { return HaskellLexerTokens.NEWTYPE; }
"of"                  { return HaskellLexerTokens.OF; }
"then"                { return HaskellLexerTokens.THEN; }
"qualified"           { return HaskellLexerTokens.QUALIFIED; }
"safe"                { return HaskellLexerTokens.SAFE; }
"type"                { return HaskellLexerTokens.TYPE; }
"unsafe"              { return HaskellLexerTokens.UNSAFE; }
"where"               { return HaskellLexerTokens.WHERE; }
"{-# INLINE"            { return HaskellLexerTokens.INLINE_PRAG; }
"{-# SPECIALISE"        { return HaskellLexerTokens.SPEC_PRAG; }
"{-# SPECIALISE_INLINE" { return HaskellLexerTokens.SPEC_INLINE_PRAG; }
"{-# SOURCE"            { return HaskellLexerTokens.SOURCE_PRAG; }
"{-# RULES"             { return HaskellLexerTokens.RULES_PRAG; }
"{-# CORE"              { return HaskellLexerTokens.CORE_PRAG; }
"{-# SCC"               { return HaskellLexerTokens.SCC_PRAG; }
"{-# GENERATED"         { return HaskellLexerTokens.GENERATED_PRAG; }
"{-# DEPRECATED"        { return HaskellLexerTokens.DEPRECATED_PRAG; }
"{-# WARNING"           { return HaskellLexerTokens.WARNING_PRAG; }
"{-# UNPACK"            { return HaskellLexerTokens.UNPACK_PRAG; }
"{-# NOUNPACK"          { return HaskellLexerTokens.NOUNPACK_PRAG; }
"{-# ANN"               { return HaskellLexerTokens.ANN_PRAG; }
"{-# VECTORISE"         { return HaskellLexerTokens.VECT_PRAG; }
"{-# VECTORISE_SCALAR"  { return HaskellLexerTokens.VECT_SCALAR_PRAG; }
"{-# NOVECTORISE"       { return HaskellLexerTokens.NOVECT_PRAG; }
"{-# MINIMAL"           { return HaskellLexerTokens.MINIMAL_PRAG; }
"{-# CTYPE"             { return HaskellLexerTokens.CTYPE; }
"{-# OVERLAPPABLE"      { return HaskellLexerTokens.OVERLAPPABLE; }
"{-# OVERLAPPING"       { return HaskellLexerTokens.OVERLAPPING; }
"{-# OVERLAPS"          { return HaskellLexerTokens.OVERLAPS; }
"{-# INCOHERENT"        { return HaskellLexerTokens.INCOHERENT; }
"#-}"                   { return HaskellLexerTokens.CLOSE_PRAG; }
"{-#".*"#-}"            { return TokenPackage.getPRAGMA(); }
(0(o|O){octit}*) |
(0(x|X){hexit}*) |
({digit}+)            { return HaskellLexerTokens.INTEGER; }

{character}           { return HaskellLexerTokens.CHAR; }
{string}              { return HaskellLexerTokens.STRING;}

"\\end{code}"         { yybegin(TEX); return TokenPackage.getBLOCK_COMMENT(); }

"''"                  { return TokenPackage.getTH_TY_QUOTE(); }
"'"                   { return TokenPackage.getTH_VAR_QUOTE(); }
{large}{idchar}*      { return HaskellLexerTokens.CONID; }
{small}{idchar}*      { return HaskellLexerTokens.VARID; }
({large}{idchar}*".")+{large}{idchar}*  { return HaskellLexerTokens.QCONID;}
({large}{idchar}*".")+{small}{idchar}*  { return HaskellLexerTokens.QVARID; }
.                     { return TokenType.BAD_CHARACTER; }