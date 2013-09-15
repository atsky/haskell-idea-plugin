package org.jetbrains.haskell.parser

import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType


open class BaseParser(public val root: IElementType, public val builder: PsiBuilder) {

    fun done(marker: PsiBuilder.Marker, result: Boolean, elementType: IElementType): Boolean {
        if (result) {
            marker.done(elementType);
        } else {
            marker.rollbackTo()
        }
        return result;
    }

    fun token(tokenType: IElementType): Boolean {
        val elementType = builder.getTokenType()
        if (elementType == tokenType) {
            builder.advanceLexer()
            return true;
        }
        return false;
    }

    fun mark(): PsiBuilder.Marker {
        return builder.mark()!!
    }

    inline fun atom(inline body: () -> Boolean): Boolean {
        val marker = mark()
        val result = body()
        if (result) {
            marker.drop();
        } else {
            marker.rollbackTo()
        }
        return result
    }

    inline fun oneOrMore(inline body: () -> Boolean): Boolean {
        val result = body()
        while (body()) {
        }
        return result
    }

    inline fun zeroOrMore(inline body: () -> Boolean): Boolean {
        while (body()) {
        }
        return true
    }

    inline fun start(elementType: IElementType, inline body: () -> Boolean): Boolean {
        val marker = mark()
        val result = body()
        return done(marker, result, elementType)
    }
}