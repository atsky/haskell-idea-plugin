package org.jetbrains.haskell.parser.token

import org.jetbrains.haskell.parser.HaskellToken
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.TokenType
import java.util.ArrayList
import org.jetbrains.grammar.HaskellLexerTokens

/**
 * Created by atsky on 3/12/14.
 */

private val KEYWORDS_MUTABLE: MutableList<HaskellToken> = ArrayList()
public val KEYWORDS: List<HaskellToken> = KEYWORDS_MUTABLE

fun keyword(token : HaskellToken) : HaskellToken {
    KEYWORDS_MUTABLE.add(token)
    return token
}

fun keyword(name : String) : HaskellToken {
    val result = HaskellToken(name)
    KEYWORDS_MUTABLE.add(result)
    return result
}

// Keywords
public val AS_KW: HaskellToken          = HaskellLexerTokens.AS
public val CASE_KW: HaskellToken        = keyword(HaskellLexerTokens.CASE)
public val CLASS_KW: HaskellToken       = keyword(HaskellLexerTokens.CLASS)
public val DATA_KW: HaskellToken        = keyword(HaskellLexerTokens.DATA)
public val DEFAULT_KW: HaskellToken     = keyword(HaskellLexerTokens.DEFAULT)
public val DERIVING_KW: HaskellToken    = keyword(HaskellLexerTokens.DERIVING)
public val DO_KW: HaskellToken          = keyword(HaskellLexerTokens.DO)
public val ELSE_KW: HaskellToken        = keyword(HaskellLexerTokens.ELSE)
public val EXPORT: HaskellToken         = keyword(HaskellLexerTokens.EXPORT)
public val HIDING_KW: HaskellToken      = HaskellLexerTokens.HIDING
public val IF_KW: HaskellToken          = keyword(HaskellLexerTokens.IF)
public val IMPORT_KW: HaskellToken      = keyword(HaskellLexerTokens.IMPORT)
public val IN_KW: HaskellToken          = keyword(HaskellLexerTokens.IN)
public val INFIX_KW: HaskellToken       = keyword(HaskellLexerTokens.INFIX)
public val INFIXL_KW: HaskellToken      = keyword(HaskellLexerTokens.INFIXL)
public val INFIXR_KW: HaskellToken      = keyword(HaskellLexerTokens.INFIXR)
public val INSTANCE_KW: HaskellToken    = keyword(HaskellLexerTokens.INSTANCE)
public val FORALL_KW: HaskellToken      = keyword(HaskellLexerTokens.FORALL)
public val FOREIGN_KW: HaskellToken     = keyword(HaskellLexerTokens.FOREIGN)
public val LET_KW: HaskellToken         = keyword(HaskellLexerTokens.LET)
public val MODULE_KW: HaskellToken      = keyword(HaskellLexerTokens.MODULE)
public val NEWTYPE_KW: HaskellToken     = keyword(HaskellLexerTokens.NEWTYPE)
public val OF_KW: HaskellToken          = keyword(HaskellLexerTokens.OF)
public val QUALIFIED_KW: HaskellToken   = HaskellLexerTokens.QUALIFIED
public val THEN_KW: HaskellToken        = keyword(HaskellLexerTokens.THEN)
public val WHERE_KW: HaskellToken       = keyword(HaskellLexerTokens.WHERE)
public val TYPE_KW: HaskellToken        = keyword(HaskellLexerTokens.TYPE)
public val SAFE: HaskellToken           = keyword(HaskellLexerTokens.SAFE)
public val UNSAFE: HaskellToken         = keyword(HaskellLexerTokens.UNSAFE)

// Operators
public val RIGHT_ARROW: HaskellToken          = HaskellLexerTokens.RARROW
public val LEFT_ARROW: HaskellToken           = HaskellLexerTokens.LARROW
public val EQUALS: HaskellToken               = HaskellLexerTokens.EQUAL
public val COLON : HaskellToken               = HaskellLexerTokens.COLON
public val DOUBLE_COLON : HaskellToken        = HaskellLexerTokens.DCOLON
public val COMMA : HaskellToken               = HaskellLexerTokens.COMMA
public val DOT : HaskellToken                 = HaskellLexerTokens.DOT
public val DOT_DOT : HaskellToken             = HaskellLexerTokens.DOTDOT
public val BACK_SLASH: HaskellToken           = HaskellLexerTokens.LAM
public val VERTICAL_BAR : HaskellToken        = HaskellLexerTokens.VBAR
public val SEMICOLON : HaskellToken           = HaskellLexerTokens.SEMI
public val AT : HaskellToken                  = HaskellLexerTokens.AT
public val QUESTION : HaskellToken            = HaskellToken("?")
public val HASH : HaskellToken                = HaskellToken("#")
public val TILDE : HaskellToken               = HaskellLexerTokens.TILDE
public val DOUBLE_ARROW : HaskellToken        = HaskellLexerTokens.DARROW
public val EXCLAMATION : HaskellToken         = HaskellLexerTokens.BANG
public val UNDERSCORE : HaskellToken          = HaskellLexerTokens.UNDERSCORE
public val BACKQUOTE : HaskellToken           = HaskellLexerTokens.BACKQUOTE




// Braces
public val LEFT_BRACKET : HaskellToken        = HaskellLexerTokens.OBRACK
public val LEFT_PAREN : HaskellToken          = HaskellLexerTokens.OPAREN
public val LEFT_BRACE : HaskellToken          = HaskellLexerTokens.OCURLY
public val RIGHT_BRACE : HaskellToken         = HaskellLexerTokens.CCURLY
public val RIGHT_BRACKET : HaskellToken       = HaskellLexerTokens.CBRACK
public val RIGHT_PAREN : HaskellToken         = HaskellLexerTokens.CPAREN


public val OPERATORS : List<HaskellToken> = listOf<HaskellToken>(
        AT,
        TILDE,
        BACK_SLASH,
        DOUBLE_ARROW,
        EXCLAMATION,
        RIGHT_ARROW,
        LEFT_ARROW,
        EQUALS,
        COMMA,
        DOT,
        DOT_DOT,
        DOUBLE_COLON,
        LEFT_PAREN,
        RIGHT_PAREN,
        LEFT_BRACE,
        RIGHT_BRACE,
        LEFT_BRACKET,
        RIGHT_BRACKET,
        LEFT_ARROW,
        SEMICOLON,
        COLON,
        VERTICAL_BAR,
        UNDERSCORE)

public val CHARACTER: HaskellToken            = HaskellLexerTokens.CHAR
public val BLOCK_COMMENT: HaskellToken        = HaskellToken("COMMENT")
public val END_OF_LINE_COMMENT : HaskellToken = HaskellToken("--")
public val ID : HaskellToken                  = HaskellLexerTokens.VARID
public val NUMBER : HaskellToken              = HaskellLexerTokens.INTEGER
public val OPERATOR_ID: HaskellToken          = HaskellLexerTokens.VARSYM
public val OPERATOR_CONS: HaskellToken        = HaskellLexerTokens.CONSYM
public val PRAGMA : HaskellToken              = HaskellToken("PRAGMA")
public val STRING : HaskellToken              = HaskellLexerTokens.STRING
public val TYPE_OR_CONS: HaskellToken         = HaskellLexerTokens.CONID
public val VIRTUAL_LEFT_PAREN : HaskellToken  = HaskellLexerTokens.VOCURLY
public val VIRTUAL_RIGHT_PAREN : HaskellToken = HaskellLexerTokens.VCCURLY
public val VIRTUAL_SEMICOLON : HaskellToken   = HaskellToken("VIRTUAL_SEMICOLON")

public val TH_VAR_QUOTE : HaskellToken        = HaskellToken("'")
public val TH_TY_QUOTE : HaskellToken         = HaskellToken("''")
public val NEW_LINE : HaskellToken            = HaskellToken("NL")

val COMMENTS: TokenSet = TokenSet.create(END_OF_LINE_COMMENT, BLOCK_COMMENT)
val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE, NEW_LINE)