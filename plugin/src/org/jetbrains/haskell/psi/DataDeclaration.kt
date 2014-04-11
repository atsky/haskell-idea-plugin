package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 4/11/14.
 */
public class DataDeclaration(node : ASTNode) : Declaration(node) {

    override fun getDeclarationName(): String? {
        return findChildByClass(javaClass<Name>())?.getText()
    }

    fun getConstructorDeclarationList() : List<ConstructorDeclaration> =
        PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())


}