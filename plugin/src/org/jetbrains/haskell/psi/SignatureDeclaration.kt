package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil


public class SignatureDeclaration(node: ASTNode) : Declaration(node) {

    override fun getDeclarationName(): String? {
        return getQNameExpression()?.getText()
    }

    fun getValuesList(): List<QVar> {
        val qVar = getQNameExpression()?.getQVar()
        if (qVar != null) {
            return listOf(qVar)
        }
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    fun getQNameExpression(): QNameExpression? =
            findChildByClass(javaClass<QNameExpression>())


}