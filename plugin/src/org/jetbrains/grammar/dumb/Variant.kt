package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType

/**
 * Created by atsky on 14/11/14.
 */
class Variant(val terms : List<Term>) {
    public var first : Set<IElementType>? = null;

    override fun toString() : String {
        val builder = StringBuilder()
        for (term in terms) {
            if (term is NonTerminal) {
                builder.append(" " + term.rule)
            } else if (term is Terminal) {
                builder.append(" '" + term.tokenType + "'")
            }
        }
        return "{" + builder.toString() + " }"
    }

    fun updateFirst() {
        if (terms.size > 0 && terms[0] is Terminal) {
            first = setOf((terms[0] as Terminal).tokenType)
        }
    }
}