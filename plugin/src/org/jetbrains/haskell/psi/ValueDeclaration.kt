package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.haskell.parser.ElementFactory
import com.intellij.psi.util.PsiTreeUtil


public class ValueDeclaration(node : ASTNode) : Declaration(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = ValueDeclaration(node)
    }

    fun getNames(): List<ValueName> =
        PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())



    override fun getDeclarationName(): String? =
            findChildByClass(javaClass<ValueName>())?.getText()

}