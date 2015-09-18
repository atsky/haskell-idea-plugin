package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiElement

/**
 * Created by atsky on 11/21/14.
 */

public class QNameExpression(node: ASTNode) : Expression(node) {
    override fun traverse(visitor: (Expression) -> Unit) {
        visitor(this)
    }

    fun getQVar(): QVar? =
            findChildByClass(QVar::class.java)


    fun getQCon(): QCon? =
            findChildByClass(QCon::class.java)


    override fun getReference(): PsiReference? {
        return null;
    }
}