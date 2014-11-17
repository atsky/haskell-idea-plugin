package org.jetbrains.grammar.dumb

/**
 * Created by atsky on 11/17/14.
 */
class ParserState(val rule : Rule,
                  val variant : Variant,
                  val ruleIndex : Int,
                  val termIndex : Int,
                  val parent : ParserState?) {

    fun next(termIndex: Int) = ParserState(rule, variant, ruleIndex + 1, termIndex, parent)

    fun nextToken() = ParserState(rule, variant, ruleIndex + 1, termIndex + 1, parent)


    override fun toString(): String {
        return "rule = ${rule.name}, rule = ${ruleIndex}, term = ${termIndex}, var = ${variant}";

    }
}