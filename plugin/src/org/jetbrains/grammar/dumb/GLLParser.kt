package org.jetbrains.grammar.dumb

import org.jetbrains.haskell.parser.lexer.HaskellLexer
import com.intellij.psi.tree.IElementType
import java.util.ArrayList

/**
 * Created by atsky on 11/17/14.
 */
class GLLParser(val grammar : Map<String, Rule>, val tokens : List<IElementType>) {

    fun parse() {
        val rule = grammar["module"]!!

        var states = ArrayList<ParserState>();

        for (variant in rule.variants) {
            states.add(ParserState(rule, variant, 0, 0, null))
        }

        @main_loop
        while (states.notEmpty) {
            val newStates = ArrayList<ParserState>();


            for (state in states) {
                if (tokens.size == state.termIndex) {
                    System.out.println("done!")
                    break@main_loop
                }
                if (state.variant.terms.size == state.ruleIndex) {
                    val parent = state.parent
                    if (parent != null) {
                        newStates.add(parent.next(state.termIndex));
                    } else {
                        throw RuntimeException()
                    }
                } else {
                    val term = state.variant.terms[state.ruleIndex]

                    when (term) {
                        is Terminal -> {
                            val currentType = tokens[state.termIndex]
                            if (currentType == term.tokenType) {
                                newStates.add(state.nextToken());
                            } else {
                                println("index=${state.termIndex}, ${currentType} != ${term.tokenType}, rule = ${state.rule.name}")
                            }
                        }
                        is NotTerminal ->
                            addNonTerminal(term, state, newStates)
                    }
                }
            }
            states = newStates;
            System.out.println("-----${states.size}-----")
        }
    }

    private fun addNonTerminal(term: NotTerminal,
                               state : ParserState,
                               newStates: ArrayList<ParserState>) {
        val nextRule = grammar[term.rule]
        if (nextRule != null) {
            for (variant in nextRule.variants) {
                newStates.add(ParserState(nextRule, variant, 0, state.termIndex, state))
            }
        }
    }
}