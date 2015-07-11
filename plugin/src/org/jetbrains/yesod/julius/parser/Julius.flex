package org.jetbrains.yesod.julius.parser;

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
%class _JuliusLexer
%implements FlexLexer

%{

%}


%function advance
%type IElementType


DIGIT            = [0-9]
WHITE_SPACE_CHAR = [\ \f\t]
EOL_COMMENT      = {INDENT}"--"[^\n]*

COMMENTS         = "//"


%%


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
//[A-Za-z0-9_-]+        { return JuliusTokenTypes.IDENTIFIER; }
{COMMENTS}            { return JuliusTokenTypes.COMMENTS; }
"\n"                  { return JuliusTokenTypes.NEWLINE; }
"}"                   { return JuliusTokenTypes.CCURLY; }
"@{"                  { return JuliusTokenTypes.AT; }
"@?{"                 { return JuliusTokenTypes.ATWITHQ; }
"#{"                  { return JuliusTokenTypes.SHARP; }
"^{"                  { return JuliusTokenTypes.HAT; }
"/*"                  { return JuliusTokenTypes.COMMENT_START; }
"*/"                  { return JuliusTokenTypes.COMMENT_END; }
"$"                   { return JuliusTokenTypes.DOLLAR; }
"_"                   { return JuliusTokenTypes.UNDERLINE; }
"var"                 { return JuliusTokenTypes.VAR; }
"function"            { return JuliusTokenTypes.FUNCTION; }
"instanceof"          { return JuliusTokenTypes.INSTANCEOF; }
"if"                  { return JuliusTokenTypes.IF; }
"else"                { return JuliusTokenTypes.ELSE; }
"switch"              { return JuliusTokenTypes.SWITCH; }
"case"                { return JuliusTokenTypes.CASE; }
":"                   { return JuliusTokenTypes.COLON; }
"break"               { return JuliusTokenTypes.BREAK; }
"default"             { return JuliusTokenTypes.DEFAULT; }
"for"                 { return JuliusTokenTypes.FOR; }
"while"               { return JuliusTokenTypes.WHILE; }
"do"                  { return JuliusTokenTypes.DO; }
"continue"            { return JuliusTokenTypes.CONTINUE; }
"new"                 { return JuliusTokenTypes.NEW; }
"delete"              { return JuliusTokenTypes.DELETE; }
"return"              { return JuliusTokenTypes.RETURN; }
"catch"               { return JuliusTokenTypes.CATCH; }
"try"                 { return JuliusTokenTypes.TRY; }
"throw"               { return JuliusTokenTypes.THROW; }
"finally"             { return JuliusTokenTypes.FINALLY; }
"in"                  { return JuliusTokenTypes.IN; }
"typeof"              { return JuliusTokenTypes.TYPEOF; }
"with"                { return JuliusTokenTypes.WITH; }
"this"                { return JuliusTokenTypes.THIS; }
"debugger"            { return JuliusTokenTypes.DEBUGGER; }
"\\"                  { return JuliusTokenTypes.BACKSLASH; }
\'([^\\\']|\\.)*\'    { return JuliusTokenTypes.STRING; }
\"([^\\\"]|\\.)*\"    { return JuliusTokenTypes.STRING; }

//"\\&"
.                     { return TokenType.BAD_CHARACTER; }

