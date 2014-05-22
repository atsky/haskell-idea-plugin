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
import org.jetbrains.haskell.psi.TypeName

/**
 * Created by atsky on 25/04/14.
 */

val TYPE_NAME : Rule = RuleBasedElementType("Simple type", TypeName) {
    TYPE_OR_CONS
}


val SIMPLETYPE : Rule = RuleBasedElementType("Simple type", SimpleType) {
    TYPE_NAME + aList(ID)
}

val TYPE_DECLARATION : Rule = RuleBasedElementType("Type declaretion", SimpleType) {
    TYPE_KW + SIMPLETYPE + EQUALS + TYPE;
}

val TYPE: Rule = lazy {
    aArrowType or aApplicationType
}

private val aArrowType : Rule = rule(ARROW_TYPE) {
    aApplicationType + RIGHT_ARROW + TYPE
}

val TYPE_REF = RuleBasedElementType("Simple type", TypeRef) { TYPE_OR_CONS }

private val aPrimitiveType : Rule = rule(TYPE_TOKEN) {

    val noBangType = ID or
                     TYPE_REF or
                     (LEFT_BRACKET plus TYPE + RIGHT_BRACKET) or
                     inParentheses(aList(TYPE, COMMA)) or
                     (LEFT_PAREN + RIGHT_PAREN)
                     maybe(EXCLAMATION) + noBangType
}

private val aApplicationType : Rule = rule(APPLICATION_TYPE) {
    aPrimitiveType + aApplicationType
} or aPrimitiveType

