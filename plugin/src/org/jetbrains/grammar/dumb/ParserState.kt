package org.jetbrains.grammar.dumb

import java.util.ArrayList

/**
 * Created by atsky on 11/17/14.
 */
class ParserState(val rule : Rule,
                  val variant : Variant,
                  val ruleIndex : Int,
                  val termIndex : Int,
                  val trees : List<ResultTree>,
                  val parent : ParserState?) {

    {
        if (rule.name == "module" && parent != null) {
            println()
        }
    }

    fun next(termIndex: Int,
             next: NonTerminalTree): ParserState {
        val newTrees = ArrayList(trees);
        newTrees.add(next)
        return ParserState(rule, variant, ruleIndex + 1, termIndex, newTrees, parent)
    }

    fun nextToken() : ParserState {
        val newTrees = ArrayList(trees);
        newTrees.add(TerminalTree((variant.terms[ruleIndex] as Terminal).tokenType))
        return ParserState(rule, variant, ruleIndex + 1, termIndex + 1, newTrees, parent)
    }

    fun getStack() : List<String> {
        val stack = ArrayList<String>()
        var currentState : ParserState? = this;
        while (currentState != null) {
            stack.add(currentState!!.rule.name)
            currentState = currentState!!.parent
        }
        return stack;
    }

    override fun toString(): String {
        return "rule = ${rule.name}, rule = ${ruleIndex}, term = ${termIndex}, var = ${variant}";
    }


    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false;
        }
        if (other !is ParserState) {
            return false;
        }
        if (rule !=  other.rule) {
            return false;
        }
        if (ruleIndex !=  other.ruleIndex) {
            return false;
        }
        if (termIndex !=  other.termIndex) {
            return false;
        }
        if (parent !=  other.parent) {
            return false;
        }
        return true;
    }

    override fun hashCode(): Int {
        var result = rule.hashCode()
        result = result * 31 + variant.hashCode()
        result = result * 31 + ruleIndex
        result = result * 31 + termIndex
        result = result * 31 + (parent?.hashCode() ?: 0)
        return result;
    }
}