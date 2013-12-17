package org.jetbrains.haskell.util.lisp

import java.util.ArrayList


public abstract class SExpression() {

    public abstract fun isListStarting(text: String) : Boolean

    public fun getValue(i: Int): String? {
        val child = get(i)
        if (child != null && child is SAtom) {
            return child.value
        }
        return null
    }
    public abstract fun get(i: Int): SExpression?

    public abstract fun toString(): String

}

public class SAtom(public val value: String) : SExpression() {

    public override fun isListStarting(text: String) : Boolean {
        return false
    }

    public override fun get(i: Int): SExpression? {
        return null;
    }

    public override fun toString(): String {
        return value
    }

}

public class SList(public val children: List<SExpression>) : SExpression() {

    public override fun isListStarting(text: String) : Boolean {
        val child = children.get(0)
        if (child is SAtom && child.value == text) {
            return true
        }
        return false
    }


    public override fun get(i: Int): SExpression? {
        if (i >= children.size()) {
            return null
        } else {
            return children.get(i)
        }
    }


    public override fun toString(): String {
        val buffer = StringBuffer()
        buffer.append("(")
        for (e : SExpression in children) {
            if (e != children[0]) {
                buffer.append(" ")
            }
            buffer.append(e)
        }
        buffer.append(")")
        return buffer.toString()
    }

}