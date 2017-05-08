package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode

/**
 * Created by atsky on 11/21/14.
 */
class ParenthesisExpression(node : ASTNode) : Expression(node) {

    fun getExpression(): Expression? =
        findChildByClass(Expression::class.java)


    override fun traverse(visitor: (Expression) -> Unit) {
        getExpression()?.traverse(visitor)
    }
}