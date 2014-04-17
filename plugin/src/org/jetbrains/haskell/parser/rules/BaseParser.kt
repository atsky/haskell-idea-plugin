package org.jetbrains.haskell.parser.rules

import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType

inline fun atom(builder: PsiBuilder, body: () -> Boolean): Boolean {
    val marker = builder.mark()!!
    val result = body()
    if (result) {
        marker.drop();
    } else {
        marker.rollbackTo()
    }
    return result
}


public fun notEmptyList(element : Rule, separator : Rule? = null) : Rule = ListRule(element, separator, false)

public fun aList(element : Rule, separator : Rule? = null) : Rule = ListRule(element, separator, true)

public fun maybe(rule : Rule) : Rule = object : Rule {

    override fun parse(builder: PsiBuilder): Boolean {
        rule.parse(builder)
        return true
    }
}

public open class BaseParser(public val root: IElementType, public val builder: PsiBuilder) {

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

    fun matchesIgnoreCase(tokenType: IElementType, text : String): Boolean {
        val elementType = builder.getTokenType()
        if (elementType == tokenType && builder.getTokenText()?.toLowerCase() == text.toLowerCase()) {
            builder.advanceLexer()
            return true;
        }
        return false;
    }



    fun matches(tokenType: IElementType, text : String): Boolean {
        val elementType = builder.getTokenType()
        if (elementType == tokenType && builder.getTokenText() == text) {
            builder.advanceLexer()
            return true;
        }
        return false;
    }

    fun mark(): PsiBuilder.Marker {
        return builder.mark()!!
    }

    inline fun atom(body: () -> Boolean): Boolean {
        val marker = mark()
        val result = body()
        if (result) {
            marker.drop();
        } else {
            marker.rollbackTo()
        }
        return result
    }

    inline fun oneOrMore(body: () -> Boolean): Boolean {
        val result = body()
        while (body()) {
        }
        return result
    }

    inline fun zeroOrMore(body: () -> Boolean): Boolean {
        while (body()) {
        }
        return true
    }

    inline fun start(elementType: IElementType, body: () -> Boolean): Boolean {
        val marker = mark()
        val result = body()
        return done(marker, result, elementType)
    }
}