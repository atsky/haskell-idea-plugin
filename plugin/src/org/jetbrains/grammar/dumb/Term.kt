package org.jetbrains.grammar.dumb

import org.jetbrains.haskell.parser.HaskellTokenType

/**
 * Created by atsky on 14/11/14.
 */
open class Term {

}

public class Terminal(val tokenType: HaskellTokenType) : Term() {
    override fun toString(): String {
        return "'" + tokenType.myName + "'"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Terminal) {
            return false
        }
        return tokenType == other.tokenType
    }

    override fun hashCode() = tokenType.hashCode()

}

public class NonTerminal(val rule: String) : Term() {
    override fun toString(): String {
        return rule
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NonTerminal) {
            return false
        }
        return rule == other.rule
    }

    override fun hashCode() = rule.hashCode()


}