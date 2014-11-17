package org.jetbrains.grammar.dumb

import java.util.ArrayList
import com.intellij.util.containers.HashMap

/**
 * Created by atsky on 15/11/14.
 */
public class GrammarBuilder() {
    public var rules : MutableMap<String, Rule> = HashMap()

    fun rule(name : String, body : RuleBuilder.() -> Unit) {
        val builder = RuleBuilder(name)
        builder.body()
        rules.put(name, Rule(name, builder.variants, builder.left))
    }
}