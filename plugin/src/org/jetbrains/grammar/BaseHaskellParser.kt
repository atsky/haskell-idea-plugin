package org.jetbrains.grammar

import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import org.jetbrains.haskell.parser.rules.ParserState
import org.jetbrains.haskell.parser.rules.ParserState.ParserMarker
import org.jetbrains.grammar.dumb.Rule
import org.jetbrains.grammar.dumb.GrammarBuilder

open class BaseHaskellParser(val state : ParserState?) {

    open fun getGrammar() : List<Rule> {
        return listOf()
    }

    fun makeMark() : ParserMarker {
        return state!!.mark()
    }

    fun token(element : IElementType) : Boolean {
        val elementType = state!!.getTokenType()
        if (elementType == element) {
            state.advanceLexer()
            return true;
        }
        return false;
    }

    fun grammar(body : GrammarBuilder.() -> Unit) : List<Rule> {
        val builder = GrammarBuilder()
        builder.body()
        return builder.rules;
    }
}