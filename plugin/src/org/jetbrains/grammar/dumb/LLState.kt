package org.jetbrains.grammar.dumb

import org.jetbrains.grammar.dumb.Rule
import org.jetbrains.grammar.dumb.ResultTree
import org.jetbrains.grammar.dumb.NonTerminalTree
import org.jetbrains.grammar.dumb.Variant
import org.jetbrains.haskell.parser.ParserState

/**
 * Created by atsky on 23/11/14.
 */
class VariantState(val variant : Variant,
                   val termIndex : Int,
                   val tree : List<ResultTree>,
                   val parserState: ParserState,
                   val parent : RuleState) {
    fun dropIndent(): VariantState {
        return VariantState(variant,
                            termIndex,
                            tree,
                            parserState.dropIndent(),
                            parent)
    }

}

class RuleState(val rule : Rule,
                val variant : Int,
                val left: Boolean,
                val bestTree : NonTerminalTree?,
                val firstNode : NonTerminalTree?,
                val parserState: ParserState,
                val parent : VariantState?) {

}