package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 11/21/14.
 */
class CaseAlternative(node : ASTNode) : ASTWrapperPsiElement(node) {
    fun getExpressions(): List<Expression> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, Expression::class.java)
    }
}