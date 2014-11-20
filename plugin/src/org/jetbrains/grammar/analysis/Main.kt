package org.jetbrains.grammar.analysis

import org.jetbrains.grammar.HaskellParser

/**
 * Created by atsky on 11/20/14.
 */
fun main(args: Array<String>) {
    val grammar = HaskellParser(null).getGrammar()
    for ((name, rule) in grammar) {
        rule.makeAnalysis(grammar);

        println("rule ${name} {")
        println(" can be empty: " + rule.canBeEmpty)
        println(" first: " + rule.first)

        for (variant in rule.variants) {
            println("  " + variant.terms)
        }
        println("}")

    }
}