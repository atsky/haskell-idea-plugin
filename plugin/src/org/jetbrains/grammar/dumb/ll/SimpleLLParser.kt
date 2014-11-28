package org.jetbrains.grammar.dumb.ll

import org.jetbrains.grammar.dumb.Rule
import com.intellij.psi.tree.IElementType
import org.jetbrains.grammar.dumb.NonTerminalTree
import org.jetbrains.grammar.dumb.ResultTree
import org.jetbrains.haskell.parser.HaskellTokenType
import org.jetbrains.grammar.dumb.Variant
import org.jetbrains.grammar.dumb.Terminal
import org.jetbrains.grammar.dumb.NonTerminal
import java.util.ArrayList
import org.jetbrains.grammar.HaskellLexerTokens
import org.jetbrains.grammar.dumb.TerminalTree
import java.util.HashMap

class SimpleLLParser(val grammar: Map<String, Rule>, var tokens: List<IElementType>) {
    val rulesCache = ArrayList<MutableMap<String, NonTerminalTree>>()

    var lastSeen = 0;
    var lastCurlyPosition = -1
    var lastCurlyState: VariantState? = null;
    var result: NonTerminalTree? = null;
    var recoveryState: VariantState? = null;

    public var writeLog: Boolean = false;

    fun parse(): NonTerminalTree? {
        val rule = grammar["module"]!!

        val ruleState = RuleState(rule, 0, false, null, null, 0, null);
        val variantState = VariantState(rule.variants[0], 0, listOf(), 0, ruleState)
        return parseState(variantState)
    }

    fun parseState(startState: VariantState): NonTerminalTree? {
        var state: VariantState? = startState;
        while (true ) {
            if (state == null) {
                if (result != null) {
                    return result;
                }
                if (lastSeen + 1 == lastCurlyPosition) {
                    val newTokens = ArrayList(tokens);
                    newTokens.add(lastCurlyPosition, HaskellLexerTokens.VCCURLY)
                    for (index in (lastCurlyPosition + 1)..newTokens.size) {
                        if (newTokens[index] == HaskellLexerTokens.VCCURLY) {
                            newTokens.remove(index);
                            break
                        }
                    }
                    tokens = newTokens
                    state = lastCurlyState
                } else if (recoveryState != null) {
                    val start = recoveryState!!.position;
                    var end = start + 1
                    while (end < tokens.size) {
                        val elementType = tokens[end]
                        if (elementType == HaskellLexerTokens.SEMI ||
                                elementType == HaskellLexerTokens.VCCURLY) {
                            break;
                        }
                        end++
                    }
                    val list = ArrayList<ResultTree>()
                    for (i in start..end - 1) {
                        list.add(TerminalTree(tokens[i] as HaskellTokenType))
                    }
                    val tree = NonTerminalTree("topdecl", 0, null, list);
                    state = nextVariant(recoveryState!!.parent, tree)
                } else {
                    return null;
                }

            }
            val current = state!!;
            val terms = current.variant.terms
            if (current.termIndex < terms.size) {
                val term = terms[current.termIndex]
                when (term) {
                    is Terminal -> {
                        if (tokens[current.position] == term.tokenType) {
                            var children = ArrayList(current.tree)
                            children.add(TerminalTree(term.tokenType))
                            lastSeen = Math.max(lastSeen, current.position)
                            state = VariantState(current.variant,
                                    current.termIndex + 1,
                                    children,
                                    current.position + 1,
                                    current.parent)
                        } else {
                            if (term.tokenType == HaskellLexerTokens.VCCURLY) {
                                if (current.position > lastCurlyPosition) {
                                    lastCurlyPosition = current.position;
                                    lastCurlyState = current;
                                }
                            }
                            state = nextVariant(current.parent, null)
                        }
                    }
                    is NonTerminal -> {
                        val ruleToParse = grammar[term.rule]!!

                        val tree = if (current.position < rulesCache.size) {
                            rulesCache[current.position][ruleToParse.name]
                        } else {
                            null
                        }
                        state = if (tree != null) {
                            ruleDone(ruleToParse, current, tree)
                        } else {
                            startRuleParsing(ruleToParse, current)
                        }
                    }
                }
            } else {
                val ruleState = current.parent
                val tree = NonTerminalTree(
                        ruleState.rule.name,
                        0,
                        current.variant.elementType,
                        current.tree)
                saveRule(ruleState, tree)
                state = nextVariant(ruleState, tree)
            }
        }
    }

    fun startRuleParsing(rule: Rule, state: VariantState): VariantState {
        //println("start ${rule.name} - pos: ${state.position}")
        val ruleState = RuleState(rule, 0, false, null, null, state.position, state)
        val variantState = VariantState(rule.variants.first!!, 0, listOf(), state.position, ruleState)
        if (rule.name == "topdecl") {
            if (recoveryState == null || recoveryState!!.position < variantState.position) {
                recoveryState = state
            }
        }
        return variantState
    }

    fun nextVariant(ruleState: RuleState,
                    tree: NonTerminalTree?): VariantState? {
        val i = ruleState.variant + 1
        val variants = ruleState.rule.variants
        val lefts = ruleState.rule.left
        val bestTree = if (ruleState.bestTree == null || (tree != null && tree.size() > ruleState.bestTree.size())) {
            tree
        } else {
            ruleState.bestTree
        }
        if ((ruleState.left && i < lefts.size) ||
                (!ruleState.left && i < variants.size)) {
            val newRuleState = RuleState(ruleState.rule,
                    i,
                    ruleState.left,
                    bestTree,
                    ruleState.firstNode,
                    ruleState.position,
                    ruleState.parent)
            if (ruleState.left) {
                return VariantState(lefts[i],
                        1,
                        listOf(ruleState.firstNode!!),
                        ruleState.position + ruleState.firstNode.size(),
                        newRuleState)
            } else {
                return VariantState(variants[i], 0, listOf(), ruleState.position, newRuleState)
            }

        } else {
            val parent = ruleState.parent
            if (parent == null) {
                result = bestTree;
                log(bestTree.toString())
                return null;
            }
            if (bestTree == null) {
                if (ruleState.firstNode != null) {
                    return ruleDone(ruleState.rule, parent, ruleState.firstNode)
                } else {
                    return nextVariant(parent.parent, null)
                }
            } else {
                if (ruleState.rule.left.isNotEmpty()) {
                    log("left ${ruleState.rule.name} - ${ruleState.position} size=${bestTree.size()}")
                    val nextRuleState = RuleState(ruleState.rule,
                            0,
                            true,
                            null,
                            bestTree,
                            ruleState.position,
                            ruleState.parent)
                    return VariantState(lefts[0],
                            1,
                            listOf(bestTree),
                            ruleState.position + bestTree.size(),
                            nextRuleState)
                }
                return ruleDone(ruleState.rule, parent, bestTree)
            }
        }
    }

    fun ruleDone(rule: Rule,
                 variantState: VariantState,
                 tree: NonTerminalTree): VariantState {

        log("done ${rule.name} - ${variantState.position} size=${tree.size()}")
        if ("importdecls" == rule.name) {
            log("test")
        }
        var children = ArrayList(variantState.tree)
        children.add(tree)
        return VariantState(variantState.variant,
                variantState.termIndex + 1,
                children,
                variantState.position + tree.size(),
                variantState.parent)
    }

    fun log(text: String) {
        if (writeLog) {
            println(text)
        }
    }

    fun saveRule(rule : RuleState, tree: NonTerminalTree) {
        val name = rule.rule.name
        val position = rule.position
        while (position >= rulesCache.size) {
            rulesCache.add(HashMap())
        }

        rulesCache[position][name] = tree
    }
}