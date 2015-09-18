package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 28/11/14.
 */
public class OperatorExpression(node: ASTNode) : Expression(node) {

    public fun getExpressions(): List<Expression> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, Expression::class.java)
    }

    override fun traverse(visitor: (Expression) -> Unit) {
        for (e in getExpressions()) {
            e.traverse(visitor)
        }
    }
}