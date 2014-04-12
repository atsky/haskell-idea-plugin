package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.SomeIdReference
import org.jetbrains.haskell.parser.ElementFactory


public class SomeId(node: ASTNode) : ASTWrapperPsiElement(node) {
    class object : ElementFactory {
        override fun create(node: ASTNode) = SomeId(node)
    }

    override fun getReference(): PsiReference? {
        return SomeIdReference(this)
    }
}