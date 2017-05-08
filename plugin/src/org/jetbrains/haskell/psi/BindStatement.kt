package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 11/21/14.
 */
class BindStatement(node : ASTNode) : Statement(node) {

    fun getExpressions() : QNameExpression? {
        val expressions = PsiTreeUtil.getChildrenOfTypeAsList(this, QNameExpression::class.java)
        return if (expressions.isEmpty()) null else expressions[0]
    }

    fun getQVar() : QVar? =
        getExpressions()?.getQVar()
}