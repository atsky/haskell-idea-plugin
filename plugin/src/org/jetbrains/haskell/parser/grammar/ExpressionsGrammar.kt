package org.jetbrains.haskell.parser.grammar

import org.jetbrains.haskell.parser.rules.lazy
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.haskell.parser.rules.rule
import org.jetbrains.haskell.parser.inParentheses
import org.jetbrains.haskell.parser.rules.notEmptyList
import org.jetbrains.haskell.parser.rules.Rule
import org.jetbrains.haskell.parser.rules.aList
import org.jetbrains.haskell.parser.rules.RuleBasedElementType
import org.jetbrains.haskell.psi.FieldUpdate
import org.jetbrains.haskell.psi.LetExpression
import org.jetbrains.haskell.psi.DoExpression
import org.jetbrains.haskell.psi.ReferenceExpression
import org.jetbrains.haskell.psi.CaseClause
import org.jetbrains.haskell.psi.DoStatement
import org.jetbrains.haskell.psi.CaseExpression
import org.jetbrains.haskell.psi.BindStatement

/**
 * Created by atsky on 21/05/14.
 */
val SIMPLE_ID = lazy {
    ID or AS_KW or HIDING_KW or QUALIFIED_KW
}


val ATOM_EXPRESSION = lazy {
    UNDERSCORE or
    COLON or
    STRING or
    CHARACTER or
    NUMBER or
    REFERENCE_EXPRESSION or
    DOT or
    OPERATOR_ID or
    FIELD_UPDATE or
    CASE_EXPRESSION or
    LET_EXPRESSION or
    DO_EXPRESSION or
    anLambdaLeftPart or
    rule(CONSTRUCTOR, { TYPE_OR_CONS }) or
    inParentheses(notEmptyList(EXPRESSION, COMMA)) or
    listLiteral
}

val FIELD_BIND: Rule = lazy {
    SIMPLE_ID + EQUALS + EXPRESSION
}

val FIELD_UPDATE: Rule = RuleBasedElementType("Field update", ::FieldUpdate) {
    LEFT_BRACE + notEmptyList(FIELD_BIND, COMMA) + RIGHT_BRACE
}


val listLiteral = lazy{
    (LEFT_BRACKET + aList(EXPRESSION, COMMA) + RIGHT_BRACKET)
}


val EXPRESSION: Rule = lazy {
    aList(ATOM_EXPRESSION, null)
}

val LET_EXPRESSION = RuleBasedElementType("Let expression", ::LetExpression) {
    LET_KW + SIMPLE_ID + EQUALS + EXPRESSION + IN_KW + EXPRESSION
}

val BIND_STATEMENT: Rule = RuleBasedElementType("Bind statement", ::BindStatement) {
    VALUE_NAME + LEFT_ARROW + EXPRESSION
}

val DO_STATEMENT: Rule = RuleBasedElementType("Do statement", ::DoStatement) {
    BIND_STATEMENT or
    (LET_KW + SIMPLE_ID + EQUALS + EXPRESSION) or
    EXPRESSION or
    untilSemicolon
}


private val DO_EXPRESSION = RuleBasedElementType("Do expression", ::DoExpression) {
    DO_KW + inBraces(aList(DO_STATEMENT, SEMICOLON_RULE))
}

private val anLambdaLeftPart = lazy {
    BACK_SLASH + notEmptyList(SIMPLE_ID) + RIGHT_ARROW
}

private val REFERENCE_EXPRESSION = RuleBasedElementType("expression", ::ReferenceExpression) {
    SIMPLE_ID
}

private val aCaseCase: Rule = RuleBasedElementType("Case clause", ::CaseClause) {
    EXPRESSION + RIGHT_ARROW + EXPRESSION
}

private val CASE_EXPRESSION = RuleBasedElementType("Case expression", ::CaseExpression) {
    val caseBody = inBraces(aList(aCaseCase, SEMICOLON_RULE))
    CASE_KW + EXPRESSION + OF_KW + caseBody
}
