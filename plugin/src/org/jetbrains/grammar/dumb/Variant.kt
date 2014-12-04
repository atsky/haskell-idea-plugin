package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import java.util.HashSet
import java.util.ArrayList
import org.jetbrains.haskell.parser.HaskellTokenType

/**
 * Created by atsky on 14/11/14.
 */

public abstract class Variant {

    fun add(tokenType : HaskellTokenType): NonTerminalVariant {
        return NonTerminalVariant(Terminal(tokenType), listOf(this))
    }

    fun add(rule: String): NonTerminalVariant {
        return NonTerminalVariant(NonTerminal(rule), listOf(this))
    }

    open fun makeAnalysis(grammar: Map<String, Rule>) {
    }

    abstract fun isCanBeEmpty(): Boolean
}

public class TerminalVariant(val elementType: IElementType?) : Variant() {
    override fun isCanBeEmpty(): Boolean {
        return true;
    }
}

public class NonTerminalVariant(val term: Term, val next: List<Variant>) : Variant() {
    public var canBeEmpty: Boolean = false;
    public var first: Set<IElementType>? = null;

    override fun isCanBeEmpty(): Boolean {
        return canBeEmpty;
    }

    override fun toString(): String {
        val builder = StringBuilder()

        if (term is NonTerminal) {
            builder.append(" " + term.rule)
        } else if (term is Terminal) {
            builder.append(" '" + term.tokenType + "'")
        }

        return "{" + builder.toString() + " }"
    }

    override fun makeAnalysis(grammar: Map<String, Rule>) {
        canBeEmpty = true;

        if (term is NonTerminal) {
            val rule = grammar[term.rule]!!

            rule.makeAnalysis(grammar)
            if (!rule.canBeEmpty) {
                canBeEmpty = false
            } else {
                for (n in next) {
                    n.makeAnalysis(grammar)
                    canBeEmpty = canBeEmpty || n.isCanBeEmpty()
                }
            }
        } else {
            canBeEmpty = false
        }

        if (term is NonTerminal) {
            val result = HashSet<IElementType>()

            val rule = grammar[term.rule]!!

            result.addAll(rule.first!!)

            if (rule.canBeEmpty) {
                for (n in next) {
                    if (n is NonTerminalVariant) {
                        result.addAll(n.first!!)
                    }
                }
            }
            first = result
        } else {
            first = setOf((term as Terminal).tokenType)
        }
    }
}