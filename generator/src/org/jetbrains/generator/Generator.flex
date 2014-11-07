package org.jetbrains.generator;


%%
%unicode
%class GrammarLexer
%type TokenType

%{
    private int commentStart;
    private int commentDepth;
%}

%xstate BLOCK_COMMENT

PLAIN_IDENTIFIER = [0-9a-zA-Z_]+
STRING           = \'[^\']*\'
EOL_COMMENT      = "--"[^\n]*

%%

<BLOCK_COMMENT> {
    "{-" {
         commentDepth++;
    }

    <<EOF>> {
        int state = yystate();
        yybegin(YYINITIAL);
        zzStartRead = commentStart;
        return TokenType.BLOCK_COMMENT;
    }

    "-}" {
        if (commentDepth > 0) {
            commentDepth--;
        }
        else {
             int state = yystate();
             yybegin(YYINITIAL);
             zzStartRead = commentStart;
             return TokenType.BLOCK_COMMENT;
        }
    }

    .|[\n\t ] {}
}


"{-" {
    yybegin(BLOCK_COMMENT);
    commentDepth = 0;
    commentStart = zzStartRead;
}


"{"                   { return TokenType.LEFT_BRACE; }
"}"                   { return TokenType.RIGHT_BRACE; }
{PLAIN_IDENTIFIER}    { return TokenType.ID; }
{STRING}              { return TokenType.STRING;}
{EOL_COMMENT}         { return TokenType.EOL_COMMENT;}
";"                   { return TokenType.SEMICOLON; }
":"                   { return TokenType.COLON; }
"|"                   { return TokenType.VBAR; }
"="                   { return TokenType.EQUAL; }
" "                   {}
\n                    {}
.                     { return TokenType.BAD_CHARACTER;}
