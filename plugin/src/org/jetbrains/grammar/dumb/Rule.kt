package org.jetbrains.grammar.dumb


class Rule(val name : String,
           val variants : List<Variant>,
           val left : List<Variant>) {

    override fun toString() : String {
        val n = name + ":\n"
        val v = "  variants: ${variants}\n"
        val l = if (left.notEmpty) "  left: ${left}\n" else ""
        return n + v + l
    }
}