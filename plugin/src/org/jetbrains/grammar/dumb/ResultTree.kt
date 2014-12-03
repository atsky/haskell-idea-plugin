package org.jetbrains.grammar.dumb

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.parser.HaskellTokenType

/**
 * Created by atsky on 11/17/14.
 */
abstract class ResultTree() {
    abstract fun size(): Int

}

class TerminalTree(val haskellToken: HaskellTokenType) : ResultTree() {
    override fun toString(): String {
        return "'" + haskellToken.myName + "'";
    }

    override fun size(): Int = 1
}

class NonTerminalTree(val rule : String,
                      val variant : Int,
                      val elementType : IElementType?,
                      val children : List<ResultTree>) : ResultTree() {

    val mySize : Int;

    {
        var size = 0;
        for (child in children) {
            size += child.size()
        }
        mySize = size
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (r in children) {
            builder.append(r.toString() + ", ")
        }
        return rule + "@" + variant + "{" + builder + "}"
    }

    fun prettyPrint(level : Int): String {
        val builder = StringBuilder()
        for (r in children) {
            builder.append("\n")
            for (i in 1..level) {
                builder.append(" ")
            }
            if (r is NonTerminalTree) {
                builder.append(r.prettyPrint(level + 1))
            } else {
                builder.append(r.toString())
            }
        }
        return rule + "@" + variant + "{" + builder + "}"
    }

    override fun size(): Int {
        return mySize;
    }
}