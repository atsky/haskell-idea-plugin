package org.jetbrains.haskell.parser

import org.jetbrains.haskell.parser.rules.Rule
import org.jetbrains.haskell.parser.rules.lazy
import org.jetbrains.haskell.parser.rules.rule
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.haskell.parser.rules.maybe
import org.jetbrains.haskell.parser.rules.aList
import org.jetbrains.haskell.parser.rules.RuleBasedElementType
import org.jetbrains.haskell.psi.SimpleType

/**
 * Created by atsky on 25/04/14.
 */

val SIMPLETYPE : Rule = RuleBasedElementType("Simple type", SimpleType) {
    rule(NAME) {TYPE_OR_CONS} + aList(ID)
}

val TYPE_DECLARATION : Rule = lazy {
    TYPE_KW + SIMPLETYPE + EQUALS + org.jetbrains.haskell.parser.aType;
}

val aType : Rule = lazy {
    org.jetbrains.haskell.parser.aArrowType or aApplicationType
}

private val aArrowType : Rule = rule(ARROW_TYPE) {
    aApplicationType + RIGHT_ARROW + aType
}

private val aPrimitiveType : Rule = rule(TYPE) {
    val noBangType = ID org.jetbrains.haskell.parser.HaskellToken.or
    TYPE_OR_CONS or
    (LEFT_BRACKET org.jetbrains.haskell.parser.HaskellToken.plus aType + RIGHT_BRACKET) or
    inParentheses(aList(aType, COMMA)) or
    (LEFT_PAREN + RIGHT_PAREN)
    maybe(EXCLAMATION) + noBangType
}

private val aApplicationType : Rule = rule(APPLICATION_TYPE) {
    aPrimitiveType + aApplicationType
} or aPrimitiveType

