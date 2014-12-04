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
    abstract fun produce(tree: NonTerminalTree?, lexerState: LexerState?): ParserState;
}

abstract class ParserResultCallBack() {
    abstract fun done(result: ParserResult): ParserState;
    abstract fun fail(): ParserState;
}

class SimpleLLParser(val grammar: Map<String, Rule>, val cached: CachedTokens) {
    var lastSeen = 0;
    var lastCurlyPosition = -1
    var lastCurlyState: ParserState? = null;
    var result: NonTerminalTree? = null;
    var recoveryState: ParserState? = null;

    public var writeLog: Boolean = false;

    fun parse(): NonTerminalTree? {
        val rule = grammar["module"]!!

        var state = parseRule(rule, newParserState(cached), object : TreeCallback() {
            override fun produce(tree: NonTerminalTree?, lexerState: LexerState?) = FinalState(tree)
        })
        while (state !is FinalState) {
            state = state.next()
        }
        return (state as FinalState).result
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
                    next.produce(tree, result.state)
                }
            }

            override fun fail(): ParserState = next.produce(null, null)
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
                    return if (state.match(term.tokenType)) {
                        val nextChildren = ArrayList(children)
                        nextChildren.add(TerminalTree(term.tokenType))
                        parseVariants(state.next(), nonTerminalVariant.next, nextChildren, next)
                    } else {
                        next.fail()
                    }
                }
                is NonTerminal -> {
                    val ruleToParse = grammar[term.rule]!!
                    //if (!ruleToParse.canBeEmpty && !ruleToParse.first!!.contains(state.getToken())) {
                    //    next.fail()
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
                return next.produce(leftTree, state)
            }
        })
    }


    fun log(text: () -> String) {
        if (writeLog) {
            println(text())
        }
    }

    inner class NextVariantStateProducer(val children: List<ResultTree>,
                                         val variant: NonTerminalVariant,
                                         val next: ParserResultCallBack) : TreeCallback() {
        override fun produce(tree: NonTerminalTree?, lexerState: LexerState?): ParserState {
            return if (tree != null) {
                val children = ArrayList(children)
                children.add(tree)
                parseVariants(lexerState!!, variant.next, children, next)
            } else {
                next.fail()
            }
        }
    }
}