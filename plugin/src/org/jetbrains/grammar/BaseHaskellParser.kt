package org.jetbrains.grammar

import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import org.jetbrains.haskell.parser.rules.ParserState
import org.jetbrains.haskell.parser.rules.ParserState.ParserMarker

open class BaseHaskellParser(val state : ParserState) {

    fun makeMark() : ParserMarker {
        return state.mark()
    }

    fun token(element : IElementType) : Boolean {
        val elementType = state.getTokenType()
        if (elementType == element) {
            state.advanceLexer()
            return true;
        }
        return false;
    }
}