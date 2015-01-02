package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.ValueReference

/**
 * Created by atsky on 11/18/14.
 */
public class QVar(node : ASTNode) : ASTWrapperPsiElement(node), PsiNamedElement {

    override fun getName(): String? {
        return getText()
    }

    override fun setName(p0: String): PsiElement? {
        throw UnsupportedOperationException()
    }

    override fun getReference(): PsiReference? {
        return ValueReference(this)
    }
}