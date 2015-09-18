package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiElement

/**
 * Created by atsky on 4/11/14.
 */
public class ConstructorDeclaration(node : ASTNode) : Declaration(node), PsiNamedElement {

    override fun getName(): String? = getDeclarationName()

    override fun setName(name: String): PsiElement? {
        throw UnsupportedOperationException()
    }

    fun getTypeVariable() : TypeVariable? {
        return findChildByClass(HaskellType::class.java)?.getLeftTypeVariable()
    }

    override fun getDeclarationName(): String? {
        return getTypeVariable()?.getText()
    }

}