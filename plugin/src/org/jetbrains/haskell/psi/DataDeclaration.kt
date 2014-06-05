package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 4/11/14.
 */
public class DataDeclaration(node : ASTNode) : Declaration(node) {

    fun getTypeName() : TypeName? {
        val simpleType = findChildByClass(javaClass<SimpleType>())
        return simpleType?.getNameElement()
    }

    override fun getDeclarationName(): String? {
        val simpleType = findChildByClass(javaClass<SimpleType>())
        return simpleType?.getNameElement()?.getText()
    }

    fun getConstructorDeclarationList() : List<ConstructorDeclaration> =
        PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())


}