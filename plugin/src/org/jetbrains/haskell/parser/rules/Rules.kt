package org.jetbrains.haskell.parser.rules

import com.intellij.lang.PsiBuilder
import org.jetbrains.haskell.parser.grammar.CONSTRUCTOR_DECLARATION
import com.intellij.psi.tree.IElementType

/**
 * Created by atsky on 4/5/14.
 */



public trait Rule {
    public fun parse(state: ParserState): Boolean

    public fun and(rule: Rule): Rule = AndRule(this, rule)

    public fun or(rule: Rule): Rule = OrRule(this, rule)

    public fun plus(rule: Rule): Rule = AndRule(this, rule)

}

private class AndRule(val first: Rule, val second: Rule) : Rule {

    override fun parse(state: ParserState): Boolean =
            atom(state) {
                first.parse(state) && second.parse(state)
            }

}

private class OrRule(val first: Rule, val second: Rule) : Rule {

    override fun parse(state: ParserState): Boolean =
            first.parse(state) || second.parse(state)


}

private class ListRule(val element: Rule, val separator: Rule?, val canBeEmpty: Boolean) : Rule {

    override fun parse(state: ParserState): Boolean =
            atom(state) {
                val result = element.parse(state)

                while (atom(state) {
                    (separator?.parse(state) ?: true) && element.parse(state)
                }) {
                }

                result
            } || canBeEmpty

}

private class LazyRule(val body : () -> Rule) : Rule {

    var rule : Rule? = null

    override fun parse(state: ParserState): Boolean {
        if (rule == null) {
            rule = body()
        }
        return rule!!.parse(state)
    }

}

public fun rule(elementType: IElementType, ruleConstructor: () -> Rule): Rule {
    return object : Rule {
        override fun parse(state: ParserState): Boolean {
            val marker = state.mark()
            val result = ruleConstructor().parse(state)

            if (result) {
                marker.done(elementType);
            } else {
                marker.rollbackTo()
            }

            return result;
        }

    }
}

public fun lazy(body : () -> Rule) : Rule = LazyRule(body)
