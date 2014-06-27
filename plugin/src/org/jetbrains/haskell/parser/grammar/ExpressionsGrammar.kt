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

/**
 * Created by atsky on 21/05/14.
 */
val simpleId = lazy {
    ID or AS_KW or HIDING_KW or QUALIFIED_KW
}


val anAtomExpression = lazy {
    UNDERSCORE or
    COLON or
    STRING or
    NUMBER or
    REFERENCE_EXPRESSION or
    DOT or
    OPERATOR_ID or
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
    simpleId + EQUALS + anExpression
}

val FIELD_UPDATE: Rule = RuleBasedElementType("Field update", ::FieldUpdate) {
    LEFT_BRACE + notEmptyList(FIELD_BIND, COMMA) + RIGHT_BRACE
}


val listLiteral = lazy{
    (LEFT_BRACKET + aList(anExpression, COMMA) + RIGHT_BRACKET)
}


val anExpression: Rule = lazy {
    aList(anAtomExpression, null)
}

val LET_EXPRESSION = RuleBasedElementType("Let expression", ::LetExpression) {
    LET_KW + simpleId + EQUALS + anExpression + IN_KW + anExpression
}

val DO_STATEMENT: Rule = RuleBasedElementType("Do statement", ::DoStatement) {
    (VALUE_NAME + LEFT_ARROW + anExpression) or
    (LET_KW + simpleId + EQUALS + anExpression) or
    anExpression or
    untilSemicolon
}


private val DO_EXPRESSION = RuleBasedElementType("Do expression", ::DoExpression) {
    DO_KW + inBraces(aList(DO_STATEMENT, SEMICOLON_RULE))
}

private val anLambdaLeftPart = lazy {
    BACK_SLASH + notEmptyList(simpleId) + RIGHT_ARROW
}

private val REFERENCE_EXPRESSION = RuleBasedElementType("expression", ::ReferenceExpression) {
    simpleId
}

private val aCaseCase: Rule = RuleBasedElementType("Case clause", ::CaseClause) {
    anExpression + RIGHT_ARROW + anExpression
}

private val CASE_EXPRESSION = RuleBasedElementType("Case expression", ::CaseExpression) {
    val caseBody = inBraces(aList(aCaseCase, SEMICOLON_RULE))
    CASE_KW + anExpression + OF_KW + caseBody
}
