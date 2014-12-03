package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.parser.HaskellTokenType
import java.util.ArrayList
import org.jetbrains.grammar.HaskellLexerTokens
import java.util.HashMap
import org.jetbrains.haskell.parser.CachedTokens
import org.jetbrains.haskell.parser.newParserState
import org.jetbrains.haskell.parser.ParserState

class SimpleLLParser(val grammar: Map<String, Rule>, val cached: CachedTokens) {
    val rulesCache = ArrayList<MutableMap<String, NonTerminalTree>>()

    var lastSeen = 0;
    var lastCurlyPosition = -1
    var lastCurlyState: VariantState? = null;
    var result: NonTerminalTree? = null;
    var recoveryState: VariantState? = null;

    public var writeLog: Boolean = false;

    fun parse(): NonTerminalTree? {
        val rule = grammar["module"]!!

        val ruleState = RuleState(rule, 0, false, null, null, newParserState(cached), null);
        val variantState = VariantState(rule.variants[0], 0, listOf(), newParserState(cached), ruleState)
        return parseState(variantState)
    }

    fun parseRule(rule : Rule, state : ParserState): NonTerminalTree? {
        var bestTree : NonTerminalTree? = null;
        for (variant in rule.variants) {
            val tree = parseVariant(rule, variant, state)
            if (tree != null && (bestTree == null || bestTree!!.size() < tree.size())) {
                bestTree = tree
            }
        }
        return bestTree;
    }

    fun parseVariant(rule : Rule,
                     variant: Variant,
                     state: ParserState): NonTerminalTree?  {
        var currentState = state
        var currentVariant : Variant = variant;

        val children = ArrayList<ResultTree>()

        while (true) {
            if (currentVariant is TerminalVariant) {
                val terminalVariant = currentVariant as TerminalVariant
                return NonTerminalTree(rule.name, 0, terminalVariant.elementType, children)
            } else {
                val nonTerminalVariant = currentVariant as NonTerminalVariant
                val term = nonTerminalVariant.term
                when (term) {
                    is Terminal -> {
                        if (currentState.match(term.tokenType)) {
                            currentState = currentState.next()
                        } else {

                        }
                    }
                    is NonTerminal -> {
                        val ruleToParse = grammar[term.rule]!!


                    }
                }
            }
        }
    }

    fun parseState(startState: VariantState): NonTerminalTree? {
        var state: VariantState? = startState;
        while (true ) {
            if (state == null) {
                if (result != null) {
                    return result;
                }
                /*
                if (lastSeen + 1 == lastCurlyPosition) {
                    state = lastCurlyState!!.dropIndent()
                } else if (recoveryState != null) {
                    val start = recoveryState!!.parserState;
                    var end = start
                    val list = ArrayList<ResultTree>()
                    while (! end.eof()) {
                        val elementType = end.getToken()
                        if (elementType == HaskellLexerTokens.SEMI ||
                                elementType == HaskellLexerTokens.VCCURLY) {
                            break;
                        }
                        list.add(TerminalTree(elementType as HaskellTokenType))
                        end = end.next()
                    }


                    val tree = NonTerminalTree("topdecl", 0, null, list);
                    state = nextVariant(recoveryState!!.parent, tree)
                    recoveryState = null
                } else {
                    return null;
                }
                */
                return null
            }
            val current = state!!;
            val currentVariant = current.variant
            if (currentVariant is NonTerminalVariant) {
                val term = currentVariant.term
                when (term) {
                    is Terminal -> {
                        if (current.parserState.match(term.tokenType)) {
                            var children = ArrayList(current.tree)
                            children.add(TerminalTree(term.tokenType))
                            lastSeen = Math.max(lastSeen, current.parserState.lexemNumber)
                            state = VariantState(currentVariant.next.first!!,
                                    0,
                                    children,
                                    current.parserState.next(),
                                    current.parent)
                        } else {
                            if (term.tokenType == HaskellLexerTokens.VCCURLY) {
                                if (current.parserState.lexemNumber > lastCurlyPosition) {
                                    lastCurlyPosition = current.parserState.lexemNumber
                                    lastCurlyState = current;
                                }
                            }
                            state = nextVariant(current.parent, null)
                        }
                    }
                    is NonTerminal -> {
                        val ruleToParse = grammar[term.rule]!!

                        val tree = if (current.parserState.lexemNumber < rulesCache.size) {
                            rulesCache[current.parserState.lexemNumber][ruleToParse.name]
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
                        (current.variant as TerminalVariant).elementType,
                        current.tree)

                state = nextVariant(ruleState, tree)
            }
        }
    }

    fun startRuleParsing(rule: Rule, state: VariantState): VariantState {
        //println("start ${rule.name} - pos: ${state.position}")
        val ruleState = RuleState(rule, 0, false, null, null, state.parserState, state)
        val variantState = VariantState(rule.variants.first!!, 0, listOf(), state.parserState, ruleState)
        if (rule.name == "topdecl") {
            if (recoveryState == null || recoveryState!!.parserState.lexemNumber < variantState.parserState.lexemNumber) {
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
                    ruleState.parserState,
                    ruleState.parent)
            if (ruleState.left) {
                return VariantState(lefts[i],
                        1,
                        listOf(ruleState.firstNode!!),
                        ruleState.parserState.skip(ruleState.firstNode),
                        newRuleState)
            } else {
                return VariantState(variants[i], 0, listOf(), ruleState.parserState, newRuleState)
            }

        } else {
            val parent = ruleState.parent
            if (parent == null) {
                result = bestTree;
                log({bestTree.toString()})
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
                    log({"left ${ruleState.rule.name} - ${ruleState.parserState.lexemNumber} size=${bestTree.size()}"})
                    val nextRuleState = RuleState(ruleState.rule,
                            0,
                            true,
                            null,
                            bestTree,
                            ruleState.parserState,
                            ruleState.parent)
                    return VariantState(lefts[0],
                            1,
                            listOf(bestTree),
                            ruleState.parserState.skip(bestTree),
                            nextRuleState)
                }
                saveRule(ruleState, bestTree)
                return ruleDone(ruleState.rule, parent, bestTree)
            }
        }
    }

    fun ruleDone(rule: Rule,
                 variantState: VariantState,
                 tree: NonTerminalTree): VariantState {

        log({"done ${rule.name} - ${variantState.parserState.lexemNumber} size=${tree.size()}"})

        var children = ArrayList(variantState.tree)
        children.add(tree)
        val nonTerminalVariant = variantState.variant as NonTerminalVariant
        return VariantState(nonTerminalVariant.next.first!!,
                0,
                children,
                variantState.parserState.skip(tree),
                variantState.parent)
    }

    fun log(text: () -> String) {
        if (writeLog) {
            println(text())
        }
    }

    fun saveRule(rule : RuleState, tree: NonTerminalTree) {
        val name = rule.rule.name
        val position = rule.parserState.lexemNumber
        while (position >= rulesCache.size) {
            rulesCache.add(HashMap())
        }

        rulesCache[position][name] = tree
    }


}