package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode

/**
 * Created by atsky on 28/11/14.
 */
public class FieldDeclaration(node : ASTNode) : Expression(node) {
    override fun traverse(visitor: (Expression) -> Unit) {
        visitor(this)
    }
}