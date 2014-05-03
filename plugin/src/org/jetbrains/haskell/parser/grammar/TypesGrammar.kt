package org.jetbrains.haskell.parser.grammar

import org.jetbrains.haskell.parser.rules.Rule
import org.jetbrains.haskell.parser.rules.lazy
import org.jetbrains.haskell.parser.rules.rule
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.haskell.parser.rules.maybe
import org.jetbrains.haskell.parser.rules.aList
import org.jetbrains.haskell.parser.rules.RuleBasedElementType
import org.jetbrains.haskell.psi.SimpleType
import org.jetbrains.haskell.parser.inParentheses
import org.jetbrains.haskell.psi.TypeRef

/**
 * Created by atsky on 25/04/14.
 */

val SIMPLETYPE : Rule = RuleBasedElementType("Simple type", SimpleType) {
    rule(NAME) {TYPE_OR_CONS} + aList(ID)
}

val TYPE_DECLARATION : Rule = lazy {
    TYPE_KW + SIMPLETYPE + EQUALS + TYPE;
}

val TYPE: Rule = lazy {
    aArrowType or aApplicationType
}

private val aArrowType : Rule = rule(ARROW_TYPE) {
    aApplicationType + RIGHT_ARROW + TYPE
}

private val aPrimitiveType : Rule = rule(TYPE_TOKEN) {
    val typeRef = RuleBasedElementType("Simple type", TypeRef) { TYPE_OR_CONS }
    val noBangType = ID or
                     typeRef or
                     (LEFT_BRACKET plus TYPE + RIGHT_BRACKET) or
                     inParentheses(aList(TYPE, COMMA)) or
                     (LEFT_PAREN + RIGHT_PAREN)
                     maybe(EXCLAMATION) + noBangType
}

private val aApplicationType : Rule = rule(APPLICATION_TYPE) {
    aPrimitiveType + aApplicationType
} or aPrimitiveType

