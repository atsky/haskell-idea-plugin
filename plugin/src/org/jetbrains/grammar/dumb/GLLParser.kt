package org.jetbrains.grammar.dumb

import org.jetbrains.haskell.parser.lexer.HaskellLexer
import com.intellij.psi.tree.IElementType
import java.util.ArrayList
import java.util.HashSet
import java.util.HashMap
import org.jetbrains.grammar.HaskellLexerTokens

/**
 * Created by atsky on 11/17/14.
 */
class GLLParser(val grammar: Map<String, Rule>, var tokens: List<IElementType>) {

    fun parse(): NonTerminalTree? {
        val rule = grammar["module"]!!

        var states = ArrayList<ParserState>();
        val wrongStates = ArrayList<ParserState>()

        for (variant in rule.variants.indices) {
            states.add(ParserState(rule, variant, 0, 0, listOf(), listOf()))
        }

        val rules = HashMap<Int, Map<String, List<ParserState>>>()

        @main_loop
        while (states.notEmpty) {
            val newStates = ArrayList<ParserState>();

            for (state in states) {
                if (state.variant().terms.size == state.ruleIndex) {
                    val tree = NonTerminalTree(state.rule.name, state.variantIndex, state.variant().elementType, state.trees)
                    for (left in state.rule.left.indices) {
                        newStates.add(ParserState(state.rule, left + state.rule.variants.size, 1, state.termIndex, listOf(tree), state.parents))
                    }
                    val parents = state.parents
                    if (!parents.empty) {
                        for (parent in parents) {
                            newStates.add(parent.next(state.termIndex, tree));
                            log("done ${state.termIndex}, stack = ${state.getStack()}")
                        }
                    } else {
                        return tree;
                    }
                } else {
                    val term = state.variant().terms[state.ruleIndex]

                    when (term) {
                        is Terminal -> {
                            if (state.termIndex < tokens.size) {
                                addTerm(newStates, wrongStates, state, term)
                            }
                        }

                        is NonTerminal ->
                            addNonTerminal(term, state, rules)
                    }
                }
            }
            for (m in rules.values()) {
                for ((ruleName, prevStates) in m) {
                    val statesSet = ArrayList(prevStates)

                    val state = prevStates[0]
                    val nextRule = grammar[ruleName]!!
                    for (variant in nextRule.variants.indices) {
                        val first = nextRule.variants[variant].first
                        if (first == null || first.contains(tokens[state.termIndex])) {
                            val nextState = ParserState(nextRule, variant, 0, state.termIndex, listOf(), ArrayList(statesSet))
                            newStates.add(nextState)
                        } else {
                            if (first.contains(HaskellLexerTokens.VCCURLY)) {
                                wrongStates.add(ParserState(nextRule, variant, 0, state.termIndex, listOf(), ArrayList(statesSet)))
                            }
                        }
                    }

                }
            }
            rules.clear();

            log("-----${states.size}-----")
            if (newStates.isEmpty()) {
                var maxIndex : Int = -1;
                for (state in wrongStates) {
                    maxIndex = Math.max(state.termIndex, maxIndex);
                }
                if (maxIndex > -1) {
                    val newTokens = ArrayList<IElementType>(tokens);
                    newTokens.add(maxIndex, HaskellLexerTokens.VCCURLY)
                    for (index in (maxIndex + 1)..newTokens.size) {
                        if (newTokens[index] == HaskellLexerTokens.VCCURLY) {
                            newTokens.remove(index);
                            break
                        }
                    }
                    tokens = newTokens
                    for (state in wrongStates) {
                        if (state.termIndex == maxIndex) {
                            newStates.add(state)
                        }
                    }
                        wrongStates.clear();
                }
            }
            states = ArrayList(newStates)
        }
        return null;
    }

    fun log(line : String) {
        //println(line)
    }


    private fun addTerm(newStates: ArrayList<ParserState>,
                        wrongStates: ArrayList<ParserState>,
                        state: ParserState,
                        term: Terminal) {
        val currentType = tokens[state.termIndex]
        if (currentType == term.tokenType) {
            newStates.add(state.nextToken());
        } else {
            if (term.tokenType == HaskellLexerTokens.VCCURLY) {
                wrongStates.add(state)
            }
            log("index=${state.termIndex}, [${currentType}] != [${term.tokenType}], stack = ${state.getStack()}")
        }
    }

    private fun addNonTerminal(term: NonTerminal,
                               state: ParserState,
                               rules: HashMap<Int, Map<String, List<ParserState>>>) {
        val ruleName = term.rule
        val nextRule = grammar[ruleName]
        if (nextRule != null) {
            val map = HashMap(rules[state.termIndex] ?: HashMap<String, List<ParserState>>())
            val list = ArrayList(map[ruleName] ?: listOf())
            list.add(state)
            map[ruleName] = list
            rules[state.termIndex] = map;
        } else {
            log("index=${state.termIndex} no rule ${ruleName}");
        }
    }
}