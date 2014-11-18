package org.jetbrains.grammar.dumb

import java.util.ArrayList

/**
 * Created by atsky on 11/17/14.
 */
class ParserState(val rule : Rule,
                  val variantIndex : Int,
                  val ruleIndex : Int,
                  val termIndex : Int,
                  val trees : List<ResultTree>,
                  val parents: List<ParserState>) {

    fun next(termIndex: Int,
             next: NonTerminalTree): ParserState {
        val newTrees = ArrayList(trees);
        newTrees.add(next)
        return ParserState(rule, variantIndex, ruleIndex + 1, termIndex, newTrees, parents)
    }

    fun variant() : Variant {
        return if (variantIndex < rule.variants.size) {
            rule.variants[variantIndex]
        } else {
            rule.left[variantIndex - rule.variants.size]
        }
    }



    fun nextToken() : ParserState {
        val newTrees = ArrayList(trees);
        newTrees.add(TerminalTree((variant().terms[ruleIndex] as Terminal).tokenType))
        return ParserState(rule, variantIndex, ruleIndex + 1, termIndex + 1, newTrees, parents)
    }

    fun getStack() : List<String> {
        val stack = ArrayList<String>()
        var currentState : ParserState? = this;
        //while (currentState != null) {
            stack.add(currentState!!.rule.name)
        //    currentState = currentState!!.parent
        //}
        return stack;
    }

    override fun toString(): String {
        return "rule = ${rule.name}, rule = ${ruleIndex}, term = ${termIndex}, var = ${variantIndex}";
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
        if (parents !=  other.parents) {
            return false;
        }
        return true;
    }

    override fun hashCode(): Int {
        var result = rule.hashCode()
        result = result * 31 + variantIndex
        result = result * 31 + ruleIndex
        result = result * 31 + termIndex
        result = result * 31 + (parents?.hashCode() ?: 0)
        return result;
    }
}