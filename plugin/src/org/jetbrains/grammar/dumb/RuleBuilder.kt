package org.jetbrains.grammar.dumb

import java.util.ArrayList
import org.jetbrains.haskell.parser.HaskellToken

/**
 * Created by atsky on 15/11/14.
 */
public class RuleBuilder(val name : String) {
    public val variants : ArrayList<Variant> = ArrayList()
    public val left : ArrayList<Variant> = ArrayList()

    fun variant(vararg args : Any?) {
        val terms = ArrayList<Term>()
        for (arg in args) {
            if (arg is String) {
                terms.add(NonTerminal(arg))
            }
            if (arg is HaskellToken) {
                terms.add(Terminal(arg))
            }
        }
        if (args.size > 0 && args[0] == name) {
            left.add(Variant(terms))
        } else {
            variants.add(Variant(terms))
        }
    }
}