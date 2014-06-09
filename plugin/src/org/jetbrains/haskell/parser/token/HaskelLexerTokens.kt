package org.jetbrains.haskell.parser.token

import org.jetbrains.haskell.parser.HaskellToken
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.TokenType
import java.util.ArrayList

/**
 * Created by atsky on 3/12/14.
 */

private val KEYWORDS_MUTABLE: MutableList<HaskellToken> = ArrayList()
public val KEYWORDS: List<HaskellToken> = KEYWORDS_MUTABLE

fun keyword(name : String) : HaskellToken {
    val result = HaskellToken(name)
    KEYWORDS_MUTABLE.add(result)
    return result
}

// Keywords
public val AS_KW: HaskellToken          = HaskellToken("as")
public val CASE_KW: HaskellToken        = keyword("case")
public val CLASS_KW: HaskellToken       = keyword("class")
public val DATA_KW: HaskellToken        = keyword("data")
public val DEFAULT_KW: HaskellToken     = keyword("default")
public val DERIVING_KW: HaskellToken    = keyword("deriving")
public val DO_KW: HaskellToken          = keyword("do")
public val ELSE_KW: HaskellToken        = keyword("else")
public val EXPORT: HaskellToken         = keyword("export")
public val HIDING_KW: HaskellToken      = HaskellToken("hiding")
public val IF_KW: HaskellToken          = keyword("if")
public val IMPORT_KW: HaskellToken      = keyword("import")
public val IN_KW: HaskellToken          = keyword("in")
public val INFIX_KW: HaskellToken       = keyword("infix")
public val INFIXL_KW: HaskellToken      = keyword("infixl")
public val INFIXR_KW: HaskellToken      = keyword("infixr")
public val INSTANCE_KW: HaskellToken    = keyword("instance")
public val FORALL_KW: HaskellToken      = keyword("forall")
public val FOREIGN_KW: HaskellToken     = keyword("foreign")
public val LET_KW: HaskellToken         = keyword("let")
public val MODULE_KW: HaskellToken      = keyword("module")
public val NEWTYPE_KW: HaskellToken     = keyword("newtype")
public val OF_KW: HaskellToken          = keyword("of")
public val QUALIFIED_KW: HaskellToken   = HaskellToken("qualified")
public val THEN_KW: HaskellToken        = keyword("then")
public val WHERE_KW: HaskellToken       = keyword("where")
public val TYPE_KW: HaskellToken        = keyword("type")
public val SAFE: HaskellToken           = keyword("safe")
public val UNSAFE: HaskellToken         = keyword("unsafe")

// Operators
public val RIGHT_ARROW: HaskellToken          = HaskellToken("->")
public val LEFT_ARROW: HaskellToken           = HaskellToken("<-")
public val EQUALS: HaskellToken               = HaskellToken("=")
public val COLON : HaskellToken               = HaskellToken(":")
public val DOUBLE_COLON : HaskellToken        = HaskellToken("::")
public val COMMA : HaskellToken               = HaskellToken(",")
public val DOT : HaskellToken                 = HaskellToken(".")
public val DOT_DOT : HaskellToken             = HaskellToken("..")
public val DOLLAR : HaskellToken              = HaskellToken("$")
public val BACK_SLASH: HaskellToken           = HaskellToken("\\")
public val VERTICAL_BAR : HaskellToken        = HaskellToken("|")
public val SEMICOLON : HaskellToken           = HaskellToken(";")
public val AT : HaskellToken                  = HaskellToken("@")
public val QUESTION : HaskellToken            = HaskellToken("?")
public val HASH : HaskellToken                = HaskellToken("#")
public val TILDE : HaskellToken               = HaskellToken("~")
public val DOUBLE_ARROW : HaskellToken        = HaskellToken("=>")
public val EXCLAMATION : HaskellToken         = HaskellToken("!")
public val UNDERSCORE : HaskellToken          = HaskellToken("_")
public val BACKQUOTE : HaskellToken           = HaskellToken("`")




// Braces
public val LEFT_BRACKET : HaskellToken        = HaskellToken("[")
public val LEFT_PAREN : HaskellToken          = HaskellToken("(")
public val LEFT_BRACE : HaskellToken          = HaskellToken("{")
public val RIGHT_BRACE : HaskellToken         = HaskellToken("}")
public val RIGHT_BRACKET : HaskellToken       = HaskellToken("]")
public val RIGHT_PAREN : HaskellToken         = HaskellToken(")")


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
        DOLLAR,
        VERTICAL_BAR,
        UNDERSCORE)

public val CHARACTER: HaskellToken            = HaskellToken("character")
public val BLOCK_COMMENT: HaskellToken        = HaskellToken("COMMENT")
public val END_OF_LINE_COMMENT : HaskellToken = HaskellToken("--")
public val ID : HaskellToken                  = HaskellToken("id")
public val NUMBER : HaskellToken              = HaskellToken("number")
public val OPERATOR_ID: HaskellToken          = HaskellToken("opertor")
public val OPERATOR_CONS: HaskellToken        = HaskellToken("opertor cons")
public val PRAGMA : HaskellToken              = HaskellToken("PRAGMA")
public val STRING : HaskellToken              = HaskellToken("string")
public val TYPE_OR_CONS: HaskellToken         = HaskellToken("type_cons")
public val VIRTUAL_LEFT_PAREN : HaskellToken  = HaskellToken("VIRTUAL_LEFT_PAREN")
public val VIRTUAL_RIGHT_PAREN : HaskellToken = HaskellToken("VIRTUAL_RIGHT_PAREN")
public val VIRTUAL_SEMICOLON : HaskellToken   = HaskellToken("VIRTUAL_SEMICOLON")

public val TH_VAR_QUOTE : HaskellToken        = HaskellToken("'")
public val TH_TY_QUOTE : HaskellToken         = HaskellToken("''")
public val NEW_LINE : HaskellToken            = HaskellToken("NL")

val COMMENTS: TokenSet = TokenSet.create(END_OF_LINE_COMMENT, BLOCK_COMMENT)
val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE, NEW_LINE)