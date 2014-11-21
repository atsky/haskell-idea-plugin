package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.QNameReference

/**
 * Created by atsky on 11/21/14.
 */

public class QNameExpression(node: ASTNode) : Expression(node) {

    fun getQVar(): QVar? =
            findChildByClass(javaClass<QVar>())


    fun getQCon(): QCon? =
            findChildByClass(javaClass<QCon>())


    override fun getReference(): PsiReference? {
        return QNameReference(this)
    }
}