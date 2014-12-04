package org.jetbrains.generator

import java.io.FileReader
import java.util.ArrayList
import org.jetbrains.generator.grammar.Grammar
import org.jetbrains.generator.grammar.Rule
import org.jetbrains.generator.grammar.Variant
import java.util.HashMap
import org.jetbrains.generator.grammar.NonFinalVariant
import org.jetbrains.generator.grammar.RuleRef

/**
 * Created by atsky on 11/7/14.
 */

fun getTokens(lexer : GrammarLexer) : List<Token> {
    val list = ArrayList<Token>();
    while (true) {
        val tokenType = lexer.yylex()
        if (tokenType == null) {
            break
        }
        if (tokenType == TokenType.BLOCK_COMMENT) {
            continue
        }
        if (tokenType == TokenType.EOL_COMMENT) {
            continue
        }
        list.add(Token(tokenType, lexer.yytext()))
    }
    return list;
}

fun main(args : Array<String>) {

    val lexer = GrammarLexer(FileReader("./plugin/haskell.grm"))

    val grammarParser = GrammarParser(getTokens(lexer))
    val grammar = grammarParser.parseGrammar()!!

    val generator = ParserGenerator(mergeGrammar(grammar))
    generator.generate()
}

fun mergeGrammar(grammar: Grammar): Grammar {
    val newRules = ArrayList<Rule>()
    for (rule in grammar.rules) {
        newRules.add(Rule(rule.name, merge(rule.variants)))
    }
    return Grammar(grammar.tokens, newRules)
}

fun merge(variants: List<Variant>): List<Variant> {
    val result = ArrayList<Variant>()
    val map = HashMap<RuleRef, ArrayList<Variant>>()
    for (variant in variants) {
        if (variant is NonFinalVariant) {
            val name = variant.atom
            if (!map.contains(name)) {
                map[name] = ArrayList<Variant>()
            }
            map[name].addAll(variant.next)
        } else {
            result.add(variant)
        }
    }
    for ((name, variants) in map) {
        result.add(NonFinalVariant(name, merge(variants)))
    }
    return result
}