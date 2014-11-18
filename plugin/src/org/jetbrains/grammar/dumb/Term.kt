package org.jetbrains.grammar.dumb

import org.jetbrains.haskell.parser.HaskellToken

/**
 * Created by atsky on 14/11/14.
 */
open class Term {

}

public class Terminal(val tokenType: HaskellToken) : Term() {

}

public class NonTerminal(val rule: String) : Term() {

}