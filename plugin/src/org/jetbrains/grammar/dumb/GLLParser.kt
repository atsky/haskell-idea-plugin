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

    public var writeLog: Boolean = false;

    fun parse(): NonTerminalTree? {
        val rule = grammar["module"]!!

        var states = ArrayList<ParserState>();
        val wrongStates = ArrayList<ParserState>()

        for (variant in rule.variants.indices) {
            states.add(ParserState(rule, variant, 0, 0, listOf(), null))
        }

        val rules = ArrayList<Map<String, RuleCache>>()

        var currentStep = 0;

        @main_loop
        while (states.notEmpty) {
            val newStates = ArrayList<ParserState>();

            for (state in states) {
                if (state.variant().terms.size == state.ruleIndex) {
                    val tree = NonTerminalTree(state.rule.name, state.variantIndex, state.variant().elementType, state.trees)
                    for (left in state.rule.left.indices) {
                        newStates.add(ParserState(state.rule, left + state.rule.variants.size, 1, state.termIndex, listOf(tree), state.parents))
                        log("add left ${state.rule.name} ${state.termIndex}")
                    }
                    val parents = state.parents
                    if (parents != null) {
                        if (parents.trees == null) {
                            parents.trees = ArrayList()
                        }
                        parents.trees!!.add(tree);
                        for (parent in parents.states) {
                            newStates.add(parent.next(state.termIndex, tree));
                            log("done ${state.termIndex}, ruleIndex = ${state.ruleIndex}, stack = ${state.getStack()}")
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
                            addNonTerminal(term, state, rules, newStates)
                    }
                }
            }
            var active = false;
            for ((ruleName, ruleCache) in rules[currentStep]) {
                if (!ruleCache.started) {
                    active = true;
                }
            }
            if (!active && newStates.isEmpty() && currentStep < rules.size - 1) {
                currentStep++;
            }
            for ((ruleName, ruleCache) in rules[currentStep]) {
                if (ruleCache.started) {
                    continue
                }
                ruleCache.started = true;

                val nextRule = grammar[ruleName]!!
                for (variant in nextRule.variants.indices) {
                    val first = nextRule.variants[variant].first
                    val parserState = ParserState(nextRule, variant, 0, currentStep, listOf(), ruleCache)
                    if (first == null || nextRule.canBeEmpty || first.contains(tokens[currentStep])) {
                        newStates.add(parserState)
                    } else {
                        if (first.contains(HaskellLexerTokens.VCCURLY)) {
                            wrongStates.add(parserState)
                        }
                    }
                }
            }

            log("-----${states.size}-----")
            if (newStates.isEmpty()) {
                var maxIndex: Int = -1;
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

    fun log(line: String) {
        if (writeLog) {
            println(line)
        }
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
                               rules: MutableList<Map<String, RuleCache>>,
                               newStates: ArrayList<ParserState>) {
        val ruleName = term.rule
        val nextRule = grammar[ruleName]
        if (nextRule != null) {
            while (rules.size <= state.termIndex) {
                rules.add(HashMap<String, RuleCache>())
            }
            val map = rules[state.termIndex]
            val ruleCache = if (!map.containsKey(ruleName)) {
                val newMap = HashMap(map)
                val newCache = RuleCache()
                newMap[ruleName] = newCache
                rules[state.termIndex] = newMap
                newCache
            } else {
                map[ruleName]!!
            }
            ruleCache.states.add(state);
            for (tree in ruleCache.trees ?: listOf<NonTerminalTree>()) {
                newStates.add(state.next(state.termIndex + tree.size(), tree));
            }
        } else {
            log("index=${state.termIndex} no rule ${ruleName}");
        }
    }
}