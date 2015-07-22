package org.jetbrains.yesod.lucius.parser;

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
%class _LuciusLexer
%implements FlexLexer

%{

%}


%function advance
%type IElementType


DIGIT            = [0-9]
WHITE_SPACE_CHAR = [\ \f\t]
EOL_COMMENT      = {INDENT}"--"[^\n]*

FUNCTION           = "attr"                      | "calc"        | "cubic-bezier"              |
                     "element"                   | "hsl"         | "hsla"                      |
                     "linear-gradient"           | "matrix"      | "matrix3d"                  |
                     "radial-gradient"           | "rect"        | "repeating-linear-gradient" |
                     "repeating-radial-gradient" | "rgb"         | "rgba"                      |
                     "rotate"                    | "rotate3d"    | "rotateX"                   |
                     "rotateY"                   | "rotateZ"     | "scale"                     |
                     "scale3d"                   | "scaleX"      | "scaleY"                    |
                     "scaleZ"                    | "skew"        | "skewX"                     |
                     "skewY"                     | "steps"       | "translate"                 |
                     "translate3d"               | "translateX"  | "translateY"                |
                     "translateZ"                | "url"

INTERPOLATION      = "^{" | "@{" | "@?{" | "#{"

%%

({WHITE_SPACE_CHAR})+ { return TokenType.WHITE_SPACE; }
{INTERPOLATION}       { return LuciusTokenTypes.INTERPOLATION; }
{FUNCTION}            { return LuciusTokenTypes.FUNCTION; }
\'([^\\\']|\\.)*\'    { return LuciusTokenTypes.STRING; }
\"([^\\\"]|\\.)*\"    { return LuciusTokenTypes.STRING; }
[0-9]+                { return LuciusTokenTypes.NUMBER; }
[A-Za-z0-9_-]+        { return LuciusTokenTypes.IDENTIFIER; }
"."[A-Za-z0-9_-]+     { return LuciusTokenTypes.DOT_IDENTIFIER; }
"#"[A-Za-z0-9_-]+     { return LuciusTokenTypes.SHARP_IDENTIFIER; }
"@"[A-Za-z0-9_.-]+    { return LuciusTokenTypes.AT_IDENTIFIER; }
":"[A-Za-z0-9_-]+     { return LuciusTokenTypes.COLON_IDENTIFIER; }
"::"[A-Za-z0-9_-]+    { return LuciusTokenTypes.CC_IDENTIFIER; }
"/*"                  { return LuciusTokenTypes.COMMENT_START; }
"*/"                  { return LuciusTokenTypes.COMMENT_END; }
"}"                   { return LuciusTokenTypes.END_INTERPOLATION; }
":"                   { return LuciusTokenTypes.COLON; }
"\n"                  { return LuciusTokenTypes.NEWLINE; }
.                     { return TokenType.BAD_CHARACTER; }

