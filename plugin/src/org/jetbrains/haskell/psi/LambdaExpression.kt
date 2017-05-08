package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 28/11/14.
 */
class LambdaExpression(node: ASTNode) : Expression(node) {

    fun getPatterns(): List<Pattern> =
            PsiTreeUtil.getChildrenOfTypeAsList(this, Pattern::class.java)


    override fun traverse(visitor: (Expression) -> Unit) {
        visitor(this)
    }
}