package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.parser.HaskellTokenType
import java.util.ArrayList
import org.jetbrains.grammar.HaskellLexerTokens
import java.util.HashMap
import org.jetbrains.haskell.parser.CachedTokens
import org.jetbrains.haskell.parser.newParserState
import org.jetbrains.haskell.parser.LexerState

class ParserResult(val children: List<ResultTree>,
                   val state: LexerState,
                   val elementType: IElementType?) {
    fun size(): Int {
        var size = 0;
        for (child in children) {
            size += child.size()
        }
        return size;
    }
}

abstract class TreeCallback() {
    abstract fun done(tree: NonTerminalTree, lexerState: LexerState): ParserState;
    abstract fun fail(): ParserState;
}

abstract class ParserResultCallBack() {
    abstract fun done(result: ParserResult): ParserState;
    abstract fun fail(): ParserState;
}



class SimpleLLParser(val grammar: Map<String, Rule>, val cached: CachedTokens) {
    var lastSeen = 0;
    var lastCurlyPosition = -1
    var lastCurlyState: RecoveryCallback? = null;
    var recoveryState: ParserState? = null;

    public var writeLog: Boolean = false;

    fun parse(): NonTerminalTree? {
        val rule = grammar["module"]!!

        var state = parseRule(rule, newParserState(cached), object : TreeCallback() {
            override fun done(tree: NonTerminalTree, lexerState: LexerState) = FinalState(tree)

            override fun fail(): ParserState = FinalState(null)
        })
        while (true) {
            if (state is FinalState) {
                if (lastSeen == lastCurlyPosition) {
                    state = lastCurlyState!!.recover()
                } else {
                    return (state as FinalState).result
                }
            }
            state = state.next()
        }

    }

    fun parseRule(rule: Rule,
                  state: LexerState,
                  next: TreeCallback): ParserState {
        //log({ "rule ${rule.name}, state = ${state.lexemNumber}" })

        return parseVariants(state, rule.variants, listOf(), object : ParserResultCallBack() {
            override fun done(result: ParserResult): ParserState {
                val tree = NonTerminalTree(rule.name, result.elementType, result.children)

                return if (rule.left.isNotEmpty()) {
                    parseLeft(rule, tree, result.state, next)
                } else {
                    next.done(tree, result.state)
                }
            }

            override fun fail(): ParserState = next.fail()
        })
    }

    fun parseVariants(state: LexerState,
                      variants: List<Variant>,
                      children: List<ResultTree>,
                      next: ParserResultCallBack): ParserState {
        if (variants.size == 1) {
            return parseVariant(state, variants.first!!, children, next)
        } else {
            return ListCombineState(null, variants, 0, state, children, next)
        }
    }

    inner class ListCombineState(
            val bestResult: ParserResult?,
            val variants: List<Variant>,
            val index: Int,
            val state: LexerState,
            val children: List<ResultTree>,
            val next: ParserResultCallBack) : ParserState() {
        override fun next(): ParserState {
            return if (index == variants.size) {
                throw RuntimeException()
            } else if (index == variants.size - 1) {
                parseVariant(state, variants[index], children, object : ParserResultCallBack() {
                    override fun done(result: ParserResult): ParserState {
                        val nextResult = if (bestResult == null || bestResult.size() < result.size()) {
                            result
                        } else {
                            bestResult
                        }
                        return if (nextResult != null) next.done(nextResult) else next.fail()
                    }

                    override fun fail(): ParserState = if (bestResult != null) next.done(bestResult) else next.fail()

                });
            } else {
                parseVariant(state, variants[index], children, object : ParserResultCallBack() {
                    override fun done(result: ParserResult): ParserState {
                        val nextResult = if (bestResult == null || bestResult.size() < result.size()) {
                            result
                        } else {
                            bestResult
                        }
                        return ListCombineState(nextResult, variants, index + 1, state, children, next)
                    }

                    override fun fail(): ParserState = ListCombineState(bestResult, variants, index + 1, state, children, next)
                })
            }
        }

    }

    fun parseVariant(state: LexerState,
                     variant: Variant,
                     children: List<ResultTree>,
                     next: ParserResultCallBack): ParserState {
        if (variant is TerminalVariant) {
            return next.done(ParserResult(children, state, variant.elementType))
        } else {
            val nonTerminalVariant = variant as NonTerminalVariant
            val term = nonTerminalVariant.term
            when (term) {
                is Terminal -> {
                    if (lastSeen < state.lexemNumber) {
                        lastSeen = state.lexemNumber
                    }
                    return if (state.match(term.tokenType)) {
                        val nextChildren = ArrayList(children)
                        nextChildren.add(TerminalTree(term.tokenType))
                        parseVariants(state.next(), nonTerminalVariant.next, nextChildren, next)
                    } else {
                        if (term.tokenType == HaskellLexerTokens.VCCURLY) {
                            if (state.lexemNumber > lastCurlyPosition) {
                                lastCurlyPosition = state.lexemNumber
                                lastCurlyState = RecoveryCallback(state, nonTerminalVariant, children, next)
                            }
                        }
                        next.fail()
                    }
                }
                is NonTerminal -> {
                    val ruleToParse = grammar[term.rule]!!
                    //if (!ruleToParse.canBeEmpty && !ruleToParse.first!!.contains(state.getToken())) {
                    //    return next.fail()
                    //}
                    return parseRule(ruleToParse, state, NextVariantStateProducer(children, variant, next))
                }
                else -> {
                    throw RuntimeException()
                }
            }
        }
    }


    fun parseLeft(rule: Rule,
                  leftTree: NonTerminalTree,
                  state: LexerState,
                  next: TreeCallback): ParserState {
        return parseVariants(state, (rule.left.first!! as NonTerminalVariant).next, listOf(leftTree), object : ParserResultCallBack() {
            override fun done(result: ParserResult): ParserState {
                val tree = NonTerminalTree(rule.name, result.elementType, result.children)
                return parseLeft(rule, tree, result.state, next)
            }

            override fun fail(): ParserState {
                return next.done(leftTree, state)
            }
        })
    }


    fun log(text: () -> String) {
        if (writeLog) {
            println(text())
        }
    }

    inner class RecoveryCallback(val state: LexerState,
                           val variant: NonTerminalVariant,
                           val children: List<ResultTree>,
                           val next: ParserResultCallBack) {
        fun recover() : ParserState {
            val lexerState = state.dropIndent().next()
            val nextChildren = ArrayList(children)
            nextChildren.add(TerminalTree(HaskellLexerTokens.VCCURLY))
            return parseVariants(lexerState, variant.next, nextChildren, next)
        }
    }

    inner class NextVariantStateProducer(val children: List<ResultTree>,
                                         val variant: NonTerminalVariant,
                                         val next: ParserResultCallBack) : TreeCallback() {
        override fun fail(): ParserState = next.fail();

        override fun done(tree: NonTerminalTree, lexerState: LexerState): ParserState {
            val children = ArrayList(children)
            children.add(tree)
            return parseVariants(lexerState, variant.next, children, next)
        }
    }
}