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
}

public class NonTerminal(val rule: String) : Term() {
    override fun toString(): String {
        return rule
    }
}