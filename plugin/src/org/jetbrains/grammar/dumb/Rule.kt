package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import java.util.ArrayList


class Rule(public val name : String,
           public val variants : List<Variant>,
           public val left : List<Variant>) {

    public var first : List<IElementType>? = null;

    override fun toString() : String {
        val n = name + ":\n"
        val v = "  variants: ${variants}\n"
        val l = if (left.notEmpty) "  left: ${left}\n" else ""
        return n + v + l
    }

    fun updateFirst() {
        val result = ArrayList<IElementType>()
        for (variant in variants) {
            variant.updateFirst()
            if (variant.first != null) {
                result.addAll(variant.first)
            } else {
                return
            }
        }
        first = result
    }
}