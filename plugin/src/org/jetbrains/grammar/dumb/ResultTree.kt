package org.jetbrains.grammar.dumb

/**
 * Created by atsky on 11/17/14.
 */
abstract class ResultTree() {

}

class TerminalTree(val haskellToken: org.jetbrains.haskell.parser.HaskellToken) : ResultTree() {
    override fun toString(): String {
        return "'" + haskellToken.myName + "'";
    }
}

class NonTerminalTree(val rule : String, val children : List<ResultTree>) : ResultTree() {
    override fun toString(): String {
        val builder = StringBuilder()
        for (r in children) {
            builder.append(r.toString() + ", ")
        }
        return rule + "{" + builder + "}"
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
        return rule + "{" + builder + "}"
    }
}