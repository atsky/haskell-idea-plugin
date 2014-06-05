package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.SomeIdReference


public class SomeId(node: ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReference(): PsiReference? {
        return SomeIdReference(this)
    }
}