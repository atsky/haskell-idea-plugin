package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import java.util.HashSet

/**
 * Created by atsky on 14/11/14.
 */
class Variant(val terms : List<Term>) {
    public var canBeEmpty : Boolean = false;
    public var first : Set<IElementType>? = null;
    public var elementType : IElementType? = null;

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

    fun makeAnalysis(grammar : Map<String, Rule>) {
        canBeEmpty = if (terms.size > 0) {
            val term = terms[0]
            if (term is NonTerminal) {
                val rule = grammar[term.rule]!!

                rule.makeAnalysis(grammar)
                rule.canBeEmpty
            } else {
                false
            }
        } else {
            true
        }

        if (terms.size == 0) {
            first = setOf()
        } else if (terms[0] is Terminal) {
            first = setOf((terms[0] as Terminal).tokenType)
        } else {
            val rule = grammar[(terms[0] as NonTerminal).rule]!!
            if (!rule.canBeEmpty) {
                first = HashSet(rule.first)
            }
        }
    }
}