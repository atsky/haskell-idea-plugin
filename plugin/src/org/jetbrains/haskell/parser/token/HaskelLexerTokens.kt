package org.jetbrains.haskell.parser.token

import org.jetbrains.haskell.parser.HaskellToken
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.TokenType

/**
 * Created by atsky on 3/12/14.
 */

public val ARROW: HaskellToken                = HaskellToken("->")
public val LEFT_ARROW: HaskellToken           = HaskellToken("<-")
public val AS_KEYWORD: HaskellToken           = HaskellToken("as")
public val ASSIGNMENT: HaskellToken           = HaskellToken("=")
public val CASE_KEYWORD: HaskellToken         = HaskellToken("case")
public val CHARACTER: HaskellToken            = HaskellToken("character")
public val CLASS_KEYWORD: HaskellToken        = HaskellToken("class")
public val COLON : HaskellToken               = HaskellToken(":")
public val COMMA : HaskellToken               = HaskellToken(",")
public val COMMENT : HaskellToken             = HaskellToken("COMMENT")
public val DATA_KEYWORD : HaskellToken        = HaskellToken("data")
public val DO_KEYWORD : HaskellToken          = HaskellToken("do")
public val DOLLAR : HaskellToken              = HaskellToken("$")
public val DOT : HaskellToken                 = HaskellToken(".")
public val ELSE_KEYWORD : HaskellToken        = HaskellToken("else")
public val END_OF_LINE_COMMENT : HaskellToken = HaskellToken("--")
public val HIDING_KEYWORD : HaskellToken      = HaskellToken("hiding")
public val IF_KEYWORD : HaskellToken          = HaskellToken("if")
public val ID : HaskellToken                  = HaskellToken("id")
public val IMPORT_KEYWORD : HaskellToken      = HaskellToken("import")
public val IN_KEYWORD : HaskellToken          = HaskellToken("in")
public val INSTANCE_KEYWORD : HaskellToken    = HaskellToken("instance")
public val LAMBDA : HaskellToken              = HaskellToken("\\")
public val LEFT_BRACKET : HaskellToken        = HaskellToken("[")
public val LEFT_PAREN : HaskellToken          = HaskellToken("(")
public val LEFT_BRACE : HaskellToken          = HaskellToken("{")
public val LET_KEYWORD : HaskellToken         = HaskellToken("let")
public val MODULE_KEYWORD : HaskellToken      = HaskellToken("module")
public val NUMBER : HaskellToken              = HaskellToken("number")
public val OF_KEYWORD : HaskellToken          = HaskellToken("of")
public val OPEN_KEYWORD : HaskellToken        = HaskellToken("open")
public val PRAGMA : HaskellToken              = HaskellToken("PRAGMA")
public val QUALIFIED_KEYWORD : HaskellToken   = HaskellToken("qualified")
public val RECORD_KEYWORD : HaskellToken      = HaskellToken("record")
public val RIGHT_BRACE : HaskellToken         = HaskellToken("}")
public val RIGHT_BRACKET : HaskellToken       = HaskellToken("]")
public val RIGHT_PAREN : HaskellToken         = HaskellToken(")")
public val SEMICOLON : HaskellToken           = HaskellToken(";")
public val STRING : HaskellToken              = HaskellToken("string")
public val THEN_KEYWORD : HaskellToken        = HaskellToken("then")
public val TYPE_OR_CONS: HaskellToken           = HaskellToken("type_cons")
public val TYPE_KEYWORD : HaskellToken        = HaskellToken("type")
public val VERTICAL_BAR : HaskellToken        = HaskellToken("|")
public val VIRTUAL_LEFT_PAREN : HaskellToken  = HaskellToken("VIRTUAL_LEFT_PAREN")
public val VIRTUAL_RIGHT_PAREN : HaskellToken = HaskellToken("VIRTUAL_RIGHT_PAREN")
public val VIRTUAL_SEMICOLON : HaskellToken   = HaskellToken("VIRTUAL_SEMICOLON")
public val WITH_KEYWORD : HaskellToken        = HaskellToken("with")
public val WHERE_KEYWORD : HaskellToken       = HaskellToken("where")

public val KEYWORDS: Array<HaskellToken> = array<HaskellToken>(
        AS_KEYWORD,
        CASE_KEYWORD,
        CLASS_KEYWORD,
        ELSE_KEYWORD,
        DO_KEYWORD,
        DATA_KEYWORD,
        HIDING_KEYWORD,
        IMPORT_KEYWORD,
        IF_KEYWORD,
        IN_KEYWORD,
        INSTANCE_KEYWORD,
        LET_KEYWORD,
        MODULE_KEYWORD,
        OF_KEYWORD,
        OPEN_KEYWORD,
        QUALIFIED_KEYWORD,
        RECORD_KEYWORD,
        RECORD_KEYWORD,
        THEN_KEYWORD,
        TYPE_KEYWORD,
        WHERE_KEYWORD)

val COMMENTS: TokenSet = TokenSet.create(END_OF_LINE_COMMENT, COMMENT)
val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE, TokenType.NEW_LINE_INDENT)