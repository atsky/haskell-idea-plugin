package org.jetbrains.haskell.parser.rules

import com.intellij.lang.PsiBuilder
import org.jetbrains.haskell.parser.grammar.CONSTRUCTOR_DECLARATION
import com.intellij.psi.tree.IElementType

/**
 * Created by atsky on 4/5/14.
 */

public trait Rule {
    public fun parse(builder: PsiBuilder): Boolean

    public fun and(rule: Rule): Rule = AndRule(this, rule)

    public fun or(rule: Rule): Rule = OrRule(this, rule)

    public fun plus(rule: Rule): Rule = AndRule(this, rule)

}

private class AndRule(val first: Rule, val second: Rule) : Rule {

    override fun parse(builder: PsiBuilder): Boolean =
            atom(builder) {
                first.parse(builder) && second.parse(builder)
            }

}

private class OrRule(val first: Rule, val second: Rule) : Rule {

    override fun parse(builder: PsiBuilder): Boolean =
            first.parse(builder) || second.parse(builder)


}

private class ListRule(val element: Rule, val separator: Rule?, val canBeEmpty: Boolean) : Rule {

    override fun parse(builder: PsiBuilder): Boolean =
            atom(builder) {
                val result = element.parse(builder)

                while (atom(builder) {
                    (separator?.parse(builder) ?: true) && element.parse(builder)
                }) {
                }

                result
            } || canBeEmpty

}

private class LazyRule(val body : () -> Rule) : Rule {

    var rule : Rule? = null

    override fun parse(builder: PsiBuilder): Boolean {
        if (rule == null) {
            rule = body()
        }
        return rule!!.parse(builder)
    }

}

public fun rule(elementType: IElementType, ruleConstructor: () -> Rule): Rule {
    return object : Rule {
        override fun parse(builder: PsiBuilder): Boolean {
            val marker = builder.mark()!!
            val result = ruleConstructor().parse(builder)

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
