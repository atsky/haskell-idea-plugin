package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import java.util.ArrayList
import java.util.HashSet


class Rule(public val name : String,
           public val variants : List<Variant>,
           public val left : List<Variant>) {

    public var done : Boolean = false;
    public var canBeEmpty : Boolean = false;
    public var first : Set<IElementType>? = null;

    override fun toString() : String {
        val n = name + ":\n"
        val v = "  variants: ${variants}\n"
        val l = if (left.isNotEmpty()) "  left: ${left}\n" else ""
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
            canBeEmpty = canBeEmpty || variant.isCanBeEmpty()
        }

        val result = HashSet<IElementType>()

        for (variant in variants) {
            if (variant is NonTerminalVariant) {
                if (variant.first != null) {
                    result.addAll(variant.first!!)
                } else {
                    return
                }
            }
        }

        if (canBeEmpty) {
            for (lVariant in left) {
                val next = (lVariant as NonTerminalVariant).next
                for (variant in next) {
                    val term = (variant as NonTerminalVariant).term
                    if (term is Terminal){
                        result.add(term.tokenType)
                    } else {
                        variant.makeAnalysis(grammar)
                        result.addAll(variant.first!!)
                    }
                }
            }
        }

        first = HashSet(result)
    }

    fun makeDeepAnalysis(grammar: Map<String, Rule>) {
        for (variant in variants) {
            variant.makeDeepAnalysis(grammar)
        }
        for (variant in left) {
            variant.makeDeepAnalysis(grammar)
        }
    }
}