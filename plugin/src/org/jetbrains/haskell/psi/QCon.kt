package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.SomeIdReference
import org.jetbrains.haskell.psi.reference.ConstructorReference
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiElement

/**
 * Created by atsky on 10/04/14.
 */
class QCon(node : ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReference(): PsiReference? {
        return ConstructorReference(this)
    }
}