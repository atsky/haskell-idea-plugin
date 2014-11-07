package org.jetbrains.generator.grammar

/**
 * Created by atsky on 11/7/14.
 */
class Variant(val atoms: List<RuleRef>) {
    override fun toString(): String {
        val result = StringBuilder()

        for (ref in atoms) {
            if (ref != atoms[0]) {
                result.append(" ")
            }
            result.append(ref)
        }
        return result.toString()
    }
}