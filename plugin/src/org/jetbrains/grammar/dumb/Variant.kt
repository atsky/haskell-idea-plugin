package org.jetbrains.grammar.dumb

/**
 * Created by atsky on 14/11/14.
 */
class Variant(val terms : List<Term>) {
    override fun toString() : String {
        val builder = StringBuilder()
        for (term in terms) {
            if (term is NotTerminal) {
                builder.append(" " + term.rule)
            } else if (term is Terminal) {
                builder.append(" '" + term.tokenType + "'")
            }
        }
        return "{" + builder.toString() + " }"
    }
}