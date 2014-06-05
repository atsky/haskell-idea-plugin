package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil


public class ValueDeclaration(node : ASTNode) : Declaration(node) {

    fun getNames(): List<ValueName> =
        PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())



    override fun getDeclarationName(): String? =
            findChildByClass(javaClass<ValueName>())?.getText()

}