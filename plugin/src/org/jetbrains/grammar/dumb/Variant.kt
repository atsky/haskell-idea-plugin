package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import java.util.HashSet

/**
 * Created by atsky on 14/11/14.
 */
class Variant(val terms: List<Term>) {
    public var canBeEmpty: Boolean = false;
    public var first: Set<IElementType>? = null;
    public var elementType: IElementType? = null;

    override fun toString(): String {
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

    fun makeAnalysis(grammar: Map<String, Rule>) {
        canBeEmpty = true;
        for (term in terms) {
            if (term is NonTerminal) {
                val rule = grammar[term.rule]!!

                rule.makeAnalysis(grammar)
                if (!rule.canBeEmpty) {
                    canBeEmpty = false
                    break
                }
            } else {
                canBeEmpty = false
                break
            }
        }

        var firstBuffer = HashSet<IElementType>()

        for (term in terms) {
            if (term is Terminal) {
                firstBuffer.add(term.tokenType)
                break
            } else {
                val rule = grammar[(term as NonTerminal).rule]!!

                firstBuffer.addAll(rule.first!!)
                if (!rule.canBeEmpty) {
                    break
                }
            }
        }
        first = firstBuffer
    }
}