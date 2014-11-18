package org.jetbrains.grammar.dumb

import org.jetbrains.haskell.parser.lexer.HaskellLexer
import com.intellij.psi.tree.IElementType
import java.util.ArrayList
import java.util.HashSet

/**
 * Created by atsky on 11/17/14.
 */
class GLLParser(val grammar : Map<String, Rule>, val tokens : List<IElementType>) {

    fun parse() {
        val rule = grammar["module"]!!

        var states = ArrayList<ParserState>();

        for (variant in rule.variants) {
            states.add(ParserState(rule, variant, 0, 0, listOf(), null))
        }

        @main_loop
        while (states.notEmpty) {
            val newStates = HashSet<ParserState>();

            for (state in states) {
                if (state.variant.terms.size == state.ruleIndex) {
                    val tree = NonTerminalTree(state.rule.name, state.trees)
                    for (left in state.rule.left) {
                        newStates.add(ParserState(state.rule, left, 1, state.termIndex, listOf(tree), state.parent))
                    }
                    val parent = state.parent
                    if (parent != null) {
                        newStates.add(parent.next(state.termIndex, tree));
                        //println("done ${state.termIndex}, stack = ${state.getStack()}")
                    } else {
                        println(tree.prettyPrint(0))
                        break@main_loop
                    }
                } else {
                    val term = state.variant.terms[state.ruleIndex]

                    when (term) {
                        is Terminal -> {
                            if (state.termIndex < tokens.size) {
                                addTerm(newStates, state, term)
                            }
                        }

                        is NotTerminal ->
                            addNonTerminal(term, state, newStates)
                    }
                }
            }
            states = ArrayList(newStates)
            System.out.println("-----${states.size}-----")
        }
    }

    private fun addTerm(newStates: HashSet<ParserState>,
                        state: ParserState,
                        term: Terminal) {
        val currentType = tokens[state.termIndex]
        if (currentType == term.tokenType) {
            newStates.add(state.nextToken());
        } else {
            //println("index=${state.termIndex}, [${currentType}] != [${term.tokenType}], stack = ${state.getStack()}")
        }
    }

    private fun addNonTerminal(term: NotTerminal,
                               state : ParserState,
                               newStates: HashSet<ParserState>) {
        val nextRule = grammar[term.rule]
        if (nextRule != null) {
            for (variant in nextRule.variants) {
                val nextState = ParserState(nextRule, variant, 0, state.termIndex, listOf(), state)
                newStates.add(nextState)
            }
        } else {
            println("index=${state.termIndex} no rule ${term.rule}");
        }
    }
}