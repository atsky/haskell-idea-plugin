package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import java.util.ArrayList
import java.util.HashSet


class Rule(public val name : String,
           public val variants : List<Variant>,
           public val left : List<Variant>) {

    public var done : Boolean = false;
    public var canBeEmpty : Boolean = false;
    public var first : List<IElementType>? = null;

    override fun toString() : String {
        val n = name + ":\n"
        val v = "  variants: ${variants}\n"
        val l = if (left.notEmpty) "  left: ${left}\n" else ""
        return n + v + l
    }

    fun makeAnalysis(grammar : Map<String, Rule>) {
        if (done) {
            return
        }
        for (variant in variants) {
            variant.makeAnalysis(grammar)
        }

        for (variant in variants) {
            canBeEmpty = canBeEmpty || variant.canBeEmpty
        }

        val result = HashSet<IElementType>()

        for (variant in variants) {
            if (variant.first != null) {
                result.addAll(variant.first!!)
            } else {
                return
            }
        }
        first = ArrayList(result)
    }
}