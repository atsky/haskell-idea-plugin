package org.jetbrains.grammar.dumb.ll

import org.jetbrains.grammar.dumb.Rule
import com.intellij.psi.tree.IElementType
import org.jetbrains.grammar.dumb.RuleCache
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
import org.jetbrains.grammar.dumb.ParserState

class SimpleLLParser(val grammar: Map<String, Rule>, var tokens: List<IElementType>) {
    val rulesCache = ArrayList<MutableMap<String, RuleCache>>()

    public var writeLog: Boolean = false;

    fun parse(): NonTerminalTree? {
        val rule = grammar["module"]!!

        val ruleState = RuleState(rule, 0, false, null, null, 0, null);
        val variantState = VariantState(rule.variants[0], 0, listOf(), 0, ruleState)
        return parseState(variantState)
    }

    fun parseState(start: VariantState): NonTerminalTree? {
        var state : VariantState? = start;
        while (true ) {
            if (state == null) {
                return null;
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
                            state = VariantState(current.variant,
                                                 current.termIndex + 1,
                                                 children,
                                                 current.position + 1,
                                                 current.parent)
                        } else {
                            state = nextVariant(current.parent, null)
                        }
                    }
                    is NonTerminal -> {
                        val ruleToParse = grammar[term.rule]!!
                        state = nextForRule(ruleToParse, current)
                    }
                }
            } else {
                val ruleState = current.parent
                val tree = NonTerminalTree(
                        ruleState.rule.name,
                        0,
                        current.variant.elementType,
                        current.tree)
                state = nextVariant(ruleState, tree)
            }
        }
    }

    fun nextForRule(rule: Rule, state: VariantState): VariantState {
        //println("start ${rule.name} - pos: ${state.position}")
        val ruleState = RuleState(rule, 0, false, null, null, state.position, state);
        return VariantState(rule.variants.first!!, 0, listOf(), state.position, ruleState);
    }

    fun nextVariant(ruleState: RuleState,
                    tree : NonTerminalTree?): VariantState? {
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
                println(bestTree)
                return null;
            }
            if (bestTree == null) {
                if (ruleState.firstNode != null) {
                    return ruleDone(ruleState, parent, ruleState.firstNode)
                } else {
                    return nextVariant(parent.parent, null)
                }
            } else {
                if (ruleState.rule.left.isNotEmpty()) {
                    println("left ${ruleState.rule.name} - ${ruleState.position} size=${bestTree.size()}")
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
                return ruleDone(ruleState, parent, bestTree)
            }
        }
    }

    fun ruleDone(ruleState: RuleState,
                 parent : VariantState,
                 tree : NonTerminalTree): VariantState {
        println("done ${ruleState.rule.name} - ${ruleState.position} size=${tree.size()}")
        var children = ArrayList(parent.tree)
        children.add(tree)
        return VariantState(parent.variant,
                parent.termIndex + 1,
                children,
                ruleState.position + tree.size(),
                parent.parent)
    }
}