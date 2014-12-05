package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 11/21/14.
 */
public class Application(node: ASTNode) : Expression(node) {

    public fun getExpressions(): List<Expression> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    override fun traverse(visitor: (Expression) -> Unit) {
        for (e in getExpressions()) {
            e.traverse(visitor)
        }
    }
}