package org.jetbrains.grammar.dumb

import java.util.ArrayList

/**
 * Created by atsky on 15/11/14.
 */
public class GrammarBuilder() {
    public var rules : MutableList<Rule> = ArrayList<Rule>()

    fun rule(name : String, body : RuleBuilder.() -> Unit) {
        val builder = RuleBuilder(name)
        builder.body()
        rules.add(Rule(name, builder.variants, builder.left))
    }
}