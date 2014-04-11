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
public val AS_KEYWORD: HaskellToken           = keyword("as")
public val CASE_KEYWORD: HaskellToken         = keyword("case")
public val CLASS_KEYWORD: HaskellToken        = keyword("class")
public val DATA_KEYWORD : HaskellToken        = keyword("data")
public val DERIVING_KEYWORD : HaskellToken    = keyword("deriving")
public val DO_KEYWORD : HaskellToken          = keyword("do")
public val ELSE_KEYWORD : HaskellToken        = keyword("else")
public val HIDING_KEYWORD : HaskellToken      = keyword("hiding")
public val IF_KEYWORD : HaskellToken          = keyword("if")
public val IMPORT_KEYWORD : HaskellToken      = keyword("import")
public val IN_KEYWORD : HaskellToken          = keyword("in")
public val INSTANCE_KEYWORD : HaskellToken    = keyword("instance")
public val LET_KEYWORD : HaskellToken         = keyword("let")
public val MODULE_KEYWORD : HaskellToken      = keyword("module")
public val OF_KEYWORD : HaskellToken          = keyword("of")
public val QUALIFIED_KEYWORD : HaskellToken   = keyword("qualified")
public val THEN_KEYWORD : HaskellToken        = keyword("then")
public val WHERE_KEYWORD : HaskellToken       = keyword("where")
public val TYPE_KEYWORD : HaskellToken        = keyword("type")


public val ARROW: HaskellToken                = HaskellToken("->")
public val LEFT_ARROW: HaskellToken           = HaskellToken("<-")
public val ASSIGNMENT: HaskellToken           = HaskellToken("=")
public val CHARACTER: HaskellToken            = HaskellToken("character")
public val COLON : HaskellToken               = HaskellToken(":")
public val COMMA : HaskellToken               = HaskellToken(",")
public val COMMENT : HaskellToken             = HaskellToken("COMMENT")
public val DOLLAR : HaskellToken              = HaskellToken("$")
public val DOT : HaskellToken                 = HaskellToken(".")
public val END_OF_LINE_COMMENT : HaskellToken = HaskellToken("--")
public val ID : HaskellToken                  = HaskellToken("id")
public val LAMBDA : HaskellToken              = HaskellToken("\\")
public val LEFT_BRACKET : HaskellToken        = HaskellToken("[")
public val LEFT_PAREN : HaskellToken          = HaskellToken("(")
public val LEFT_BRACE : HaskellToken          = HaskellToken("{")
public val NUMBER : HaskellToken              = HaskellToken("number")
public val PRAGMA : HaskellToken              = HaskellToken("PRAGMA")
public val RIGHT_BRACE : HaskellToken         = HaskellToken("}")
public val RIGHT_BRACKET : HaskellToken       = HaskellToken("]")
public val RIGHT_PAREN : HaskellToken         = HaskellToken(")")
public val SEMICOLON : HaskellToken           = HaskellToken(";")
public val STRING : HaskellToken              = HaskellToken("string")
public val TYPE_OR_CONS: HaskellToken         = HaskellToken("type_cons")
public val VERTICAL_BAR : HaskellToken        = HaskellToken("|")
public val VIRTUAL_LEFT_PAREN : HaskellToken  = HaskellToken("VIRTUAL_LEFT_PAREN")
public val VIRTUAL_RIGHT_PAREN : HaskellToken = HaskellToken("VIRTUAL_RIGHT_PAREN")
public val VIRTUAL_SEMICOLON : HaskellToken   = HaskellToken("VIRTUAL_SEMICOLON")

public val OPERATORS : List<HaskellToken> = listOf<HaskellToken>(
        ARROW,
        ASSIGNMENT,
        COMMA,
        DOT,
        SEMICOLON,
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
        VERTICAL_BAR)


val COMMENTS: TokenSet = TokenSet.create(END_OF_LINE_COMMENT, COMMENT)
val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE, TokenType.NEW_LINE_INDENT)