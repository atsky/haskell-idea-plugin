package org.jetbrains.grammar.dumb

import org.jetbrains.grammar.dumb.Rule
import org.jetbrains.grammar.dumb.ResultTree
import org.jetbrains.grammar.dumb.NonTerminalTree
import org.jetbrains.grammar.dumb.Variant

/**
 * Created by atsky on 23/11/14.
 */
class VariantState(val variant : Variant,
                   val termIndex : Int,
                   val tree : List<ResultTree>,
                   val position : Int,
                   val parent : RuleState) {

}

class RuleState(val rule : Rule,
                val variant : Int,
                val left: Boolean,
                val bestTree : NonTerminalTree?,
                val firstNode : NonTerminalTree?,
                val position : Int,
                val parent : VariantState?) {

}