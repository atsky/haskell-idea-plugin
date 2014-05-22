package org.jetbrains.haskell.parser.grammar

import org.jetbrains.haskell.parser.rules.lazy
import org.jetbrains.haskell.parser.token.UNDERSCORE
import org.jetbrains.haskell.parser.token.COLON
import org.jetbrains.haskell.parser.token.STRING
import org.jetbrains.haskell.parser.token.NUMBER
import org.jetbrains.haskell.parser.token.DOT
import org.jetbrains.haskell.parser.token.OPERATOR
import org.jetbrains.haskell.parser.token.DOLLAR
import org.jetbrains.haskell.parser.rules.rule
import org.jetbrains.haskell.parser.token.TYPE_OR_CONS
import org.jetbrains.haskell.parser.inParentheses
import org.jetbrains.haskell.parser.rules.notEmptyList
import org.jetbrains.haskell.parser.token.COMMA
import org.jetbrains.haskell.parser.rules.Rule
import org.jetbrains.haskell.parser.rules.aList
import org.jetbrains.haskell.parser.token.ID
import org.jetbrains.haskell.parser.token.EQUALS
import org.jetbrains.haskell.parser.rules.RuleBasedElementType
import org.jetbrains.haskell.psi.FieldUpdate
import org.jetbrains.haskell.parser.token.LEFT_BRACE
import org.jetbrains.haskell.parser.token.RIGHT_BRACE
import org.jetbrains.haskell.parser.token.LEFT_BRACKET
import org.jetbrains.haskell.parser.token.RIGHT_BRACKET
import org.jetbrains.haskell.psi.LetExpression
import org.jetbrains.haskell.parser.token.LET_KW
import org.jetbrains.haskell.parser.token.IN_KW
import org.jetbrains.haskell.psi.DoExpression
import org.jetbrains.haskell.parser.token.DO_KW
import org.jetbrains.haskell.parser.token.VIRTUAL_LEFT_PAREN
import org.jetbrains.haskell.parser.token.VIRTUAL_SEMICOLON
import org.jetbrains.haskell.parser.token.VIRTUAL_RIGHT_PAREN
import org.jetbrains.haskell.parser.token.BACK_SLASH
import org.jetbrains.haskell.parser.token.RIGHT_ARROW
import org.jetbrains.haskell.psi.ReferenceExpression
import org.jetbrains.haskell.psi.CaseClause
import org.jetbrains.haskell.parser.token.CASE_KW
import org.jetbrains.haskell.parser.token.OF_KW
import org.jetbrains.haskell.psi.DoStatement
import org.jetbrains.haskell.parser.token.LEFT_ARROW

/**
 * Created by atsky on 21/05/14.
 */
val anAtomExpression = lazy {
    UNDERSCORE or
    COLON or
    STRING or
    NUMBER or
    REFERENCE_EXPRESSION or
    DOT or
    OPERATOR or
    DOLLAR or
    FIELD_UPDATE or
    CASE_EXPRESSION or
    LET_EXPRESSION or
    DO_EXPRESSION or
    anLambdaLeftPart or
    rule(CONSTRUCTOR, { TYPE_OR_CONS }) or
    inParentheses(notEmptyList(anExpression, COMMA)) or
    listLiteral
}

val FIELD_BIND: Rule = lazy {
    ID + EQUALS + anExpression
}

val FIELD_UPDATE: Rule = RuleBasedElementType("Field update", FieldUpdate) {
    LEFT_BRACE + notEmptyList(FIELD_BIND, COMMA) + RIGHT_BRACE
}


val listLiteral = lazy{
    (LEFT_BRACKET + aList(anExpression, COMMA) + RIGHT_BRACKET)
}


val anExpression: Rule = lazy {
    aList(anAtomExpression, null)
}

val LET_EXPRESSION = RuleBasedElementType("Let expression", LetExpression) {
    LET_KW + ID + EQUALS + anExpression + IN_KW + anExpression
}

val DO_STATEMENT: Rule = RuleBasedElementType("Do statement", DoStatement) {
    (VALUE_NAME + LEFT_ARROW + anExpression) or
    (LET_KW + ID + EQUALS + anExpression) or
    anExpression or
    untilSemicolon
}


private val DO_EXPRESSION = RuleBasedElementType("Do expression", DoExpression) {
    DO_KW + VIRTUAL_LEFT_PAREN + aList(DO_STATEMENT, untilSemicolon + VIRTUAL_SEMICOLON) + VIRTUAL_RIGHT_PAREN
}

private val anLambdaLeftPart = lazy {
    BACK_SLASH + notEmptyList(ID) + RIGHT_ARROW
}

private val REFERENCE_EXPRESSION = RuleBasedElementType("expression", ReferenceExpression) {
    ID
}

private val aCaseCase: Rule = RuleBasedElementType("Case clause", CaseClause) {
    anExpression + RIGHT_ARROW + anExpression
}

private val CASE_EXPRESSION = RuleBasedElementType("Case expression", CaseClause) {
    val caseBody = VIRTUAL_LEFT_PAREN + aList(aCaseCase, VIRTUAL_SEMICOLON) + VIRTUAL_RIGHT_PAREN
    CASE_KW + anExpression + OF_KW + caseBody
}
