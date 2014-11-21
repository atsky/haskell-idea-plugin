package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.QNameExpression

/**
 * Created by atsky on 4/25/14.
 */
class ValueReference(val referenceExpression: QNameExpression) : PsiReferenceBase<QNameExpression>(
        referenceExpression,
        TextRange(0, referenceExpression.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        return null
    }


    override fun getVariants(): Array<Any> = array()

}