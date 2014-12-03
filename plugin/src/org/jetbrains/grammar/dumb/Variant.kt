package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import java.util.HashSet
import java.util.ArrayList

/**
 * Created by atsky on 14/11/14.
 */
class Variant(val terms: List<Term>) {
    public var canBeEmpty: Boolean = false;
    public var first: Set<List<IElementType>>? = null;
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

        val result = HashSet<List<IElementType>>()

        var prefixes = HashSet<List<IElementType>>()
        prefixes.add(listOf())

        for (term in terms) {
            val nextPrefixes = HashSet<List<IElementType>>()
            val firstBuffer = HashSet<List<IElementType>>()
            if (term is Terminal) {
                firstBuffer.add(listOf(term.tokenType))
            } else {
                val rule = grammar[(term as NonTerminal).rule]!!
                //rule.makeAnalysis(grammar)
                if (rule.first != null) {
                    firstBuffer.addAll(rule.first!!)
                }
            }
            for (prefix in prefixes) {
                for (next in firstBuffer) {
                    val tmp = prefix + next
                    if (tmp.size >= 2) {
                        result.add(ArrayList(tmp.subList(0, 2)))
                    } else {
                        nextPrefixes.add(tmp)
                    }
                }
            }
            prefixes = nextPrefixes
            if (prefixes.isEmpty()) {
                break
            }
        }
        result.addAll(prefixes)
        first = result
    }
}