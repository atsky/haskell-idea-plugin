package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil


class SignatureDeclaration(node: ASTNode) : Declaration(node) {

    override fun getDeclarationName(): String? {
        return getQNameExpression()?.text
    }

    fun getValuesList(): List<QVar> {
        val qVar = getQNameExpression()?.getQVar()
        if (qVar != null) {
            return listOf(qVar)
        }
        return PsiTreeUtil.getChildrenOfTypeAsList(this, QVar::class.java)
    }

    fun getQNameExpression(): QNameExpression? =
            findChildByClass(QNameExpression::class.java)


}