package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.ValueReference
import org.jetbrains.haskell.psi.reference.TypeReference

/**
 * Created by atsky on 4/11/14.
 */
public class TypeVariable(node: ASTNode) : ASTWrapperPsiElement(node) {

    fun getNameText() : String? {
        return getText()
    }

    override fun getReference(): PsiReference? {
        return TypeReference(this)
    }
}