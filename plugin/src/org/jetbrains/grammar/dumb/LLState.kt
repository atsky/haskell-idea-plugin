package org.jetbrains.grammar.dumb

import org.jetbrains.grammar.dumb.Rule
import org.jetbrains.grammar.dumb.ResultTree
import org.jetbrains.grammar.dumb.NonTerminalTree
import org.jetbrains.grammar.dumb.Variant
import org.jetbrains.haskell.parser.LexerState

/**
 * Created by atsky on 23/11/14.
 */
abstract class ParserState {
    abstract fun next() : ParserState
}

class FinalState(val result : NonTerminalTree?) : ParserState() {
    override fun next(): ParserState {
        return this
    }

}

