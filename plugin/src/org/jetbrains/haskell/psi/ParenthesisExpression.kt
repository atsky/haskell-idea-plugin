package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode

/**
 * Created by atsky on 11/21/14.
 */
public class ParenthesisExpression(node : ASTNode) : Expression(node) {
    override fun traverse(visitor: (Expression) -> Unit) {
        visitor(this)
    }
}