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

KEYWORD          = "abstract" |	"arguments" | "boolean"	   | "break"	 | "byte"         |
                   "case"	  | "catch"	    | "char"	   | "class"	 | "const"        |
                   "continue" |	"debugger"	| "default"	   | "delete"	 | "do"           |
                   "double"	  | "else"	    | "enum"	   | "eval"	     | "export"       |
                   "extends"  |	"false"	    | "final"	   | "finally"	 | "float"        |
                   "for"	  | "function"	| "goto"	   | "if"	     | "implements"   |
                   "import"	  | "in"	    | "instanceof" | "int"	     | "interface"    |
                   "let"	  | "long"   	| "native"	   | "new"	     | "null"         |
                   "package"  |	"private"	| "protected"  | "public"	 | "return"       |
                   "short"	  | "static"	| "super"	   | "switch"	 | "synchronized" |
                   "this"	  | "throw"	    | "throws"	   | "transient" | "true"         |
                   "try"	  | "typeof"	| "var"        | "void"	     | "volatile"     |
                   "while"	  | "with"	    | "yield"

INTERPOLATION     = "^{" | "@{" | "@?{" | "#{"

%%


({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
{KEYWORD}             { return JuliusTokenTypes.KEYWORD; }
{INTERPOLATION}       { return JuliusTokenTypes.INTERPOLATION; }
\'([^\\\']|\\.)*\'    { return JuliusTokenTypes.STRING; }
\"([^\\\"]|\\.)*\"    { return JuliusTokenTypes.STRING; }
"."[A-Za-z0-9_-]+     { return JuliusTokenTypes.DOT_IDENTIFIER; }
[0-9]+                { return JuliusTokenTypes.NUMBER; }
"//"                  { return JuliusTokenTypes.COMMENT; }
"/*"                  { return JuliusTokenTypes.COMMENT_START; }
"*/"                  { return JuliusTokenTypes.COMMENT_END; }
"}"                   { return JuliusTokenTypes.END_INTERPOLATION; }
"\n"                  { return JuliusTokenTypes.NEWLINE; }
.                     { return TokenType.BAD_CHARACTER; }

