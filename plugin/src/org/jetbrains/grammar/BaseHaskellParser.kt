package org.jetbrains.grammar

import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker

open class BaseHaskellParser(val state : PsiBuilder) {

    fun makeMark() : Marker {
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