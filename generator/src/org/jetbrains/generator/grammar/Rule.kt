package org.jetbrains.generator.grammar

/**
 * Created by atsky on 11/7/14.
 */
class Rule(name : String, val variants : List<Variant>) : AbstractRule(name) {
    override fun toString(): String {
        val result = StringBuilder()
        result.append(name).append(" : ")
        for (variant in variants) {
            if (variant != variants[0]) {
                result.append(" | ")
            }
            result.append(variant)
        }
        return result.toString()
    }
}