package org.jetbrains.haskell.parser.token

import org.jetbrains.haskell.parser.HaskellTokenType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.TokenType
import java.util.ArrayList
import org.jetbrains.grammar.HaskellLexerTokens
import org.jetbrains.haskell.parser.cpp.CPPTokens

/**
 * Created by atsky on 3/12/14.
 */

public val KEYWORDS: List<HaskellTokenType> = listOf(
        HaskellLexerTokens.CASE,
        HaskellLexerTokens.CLASS,
        HaskellLexerTokens.DATA,
        HaskellLexerTokens.DEFAULT,
        HaskellLexerTokens.DERIVING,
        HaskellLexerTokens.DO,
        HaskellLexerTokens.ELSE,
        HaskellLexerTokens.EXPORT,
        HaskellLexerTokens.IF,
        HaskellLexerTokens.IMPORT,
        HaskellLexerTokens.IN,
        HaskellLexerTokens.INFIX,
        HaskellLexerTokens.INFIXL,
        HaskellLexerTokens.INFIXR,
        HaskellLexerTokens.INSTANCE,
        HaskellLexerTokens.FORALL,
        HaskellLexerTokens.FOREIGN,
        HaskellLexerTokens.LET,
        HaskellLexerTokens.MODULE,
        HaskellLexerTokens.NEWTYPE,
        HaskellLexerTokens.OF,
        HaskellLexerTokens.THEN,
        HaskellLexerTokens.WHERE,
        HaskellLexerTokens.TYPE,
        HaskellLexerTokens.SAFE,
        HaskellLexerTokens.UNSAFE)


public val OPERATORS: List<HaskellTokenType> = listOf<HaskellTokenType>(
        HaskellLexerTokens.AT,
        HaskellLexerTokens.TILDE,
        HaskellLexerTokens.LAM,
        HaskellLexerTokens.DARROW,
        HaskellLexerTokens.BANG,
        HaskellLexerTokens.RARROW,
        HaskellLexerTokens.LARROW,
        HaskellLexerTokens.EQUAL,
        HaskellLexerTokens.COMMA,
        HaskellLexerTokens.DOT,
        HaskellLexerTokens.DOTDOT,
        HaskellLexerTokens.DCOLON,
        HaskellLexerTokens.OPAREN,
        HaskellLexerTokens.CPAREN,
        HaskellLexerTokens.OCURLY,
        HaskellLexerTokens.CCURLY,
        HaskellLexerTokens.OBRACK,
        HaskellLexerTokens.CBRACK,
        HaskellLexerTokens.SEMI,
        HaskellLexerTokens.COLON,
        HaskellLexerTokens.VBAR,
        HaskellLexerTokens.UNDERSCORE)


public val BLOCK_COMMENT: HaskellTokenType = HaskellTokenType("COMMENT")
public val END_OF_LINE_COMMENT: HaskellTokenType = HaskellTokenType("--")
public val PRAGMA: HaskellTokenType = HaskellTokenType("PRAGMA")

public val NEW_LINE: HaskellTokenType = HaskellTokenType("NL")

val COMMENTS: TokenSet = TokenSet.create(
        END_OF_LINE_COMMENT,
        BLOCK_COMMENT,
        PRAGMA,
        CPPTokens.IF,
        CPPTokens.ENDIF,
        CPPTokens.ELSE,
        CPPTokens.IFDEF)
val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE, NEW_LINE)