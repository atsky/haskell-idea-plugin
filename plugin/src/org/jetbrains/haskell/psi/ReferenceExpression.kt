package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.SomeIdReference
import org.jetbrains.haskell.psi.reference.ValueReference

/**
 * Created by atsky on 4/25/14.
 */
public class ReferenceExpression(node: ASTNode) : Expression(node) {

    override fun getReference(): PsiReference? {
        return ValueReference(this)
    }

}