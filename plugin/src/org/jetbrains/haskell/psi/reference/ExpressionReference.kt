package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.ReferenceExpression

/**
 * Created by atsky on 4/25/14.
 */
class ExpressionReference(val referenceExpression: ReferenceExpression) : PsiReferenceBase<ReferenceExpression>(
        referenceExpression,
        TextRange(0, referenceExpression.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        for (value in getElement()!!.getVisibleValues()) {
            if (value.getText() == referenceExpression.getText()) {
                return value
            }
        }
        return null
    }


    override fun getVariants(): Array<Any> = getElement()!!.getVisibleValues().copyToArray()

}