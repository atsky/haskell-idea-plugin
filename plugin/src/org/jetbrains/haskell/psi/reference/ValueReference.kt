package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.QNameExpression
import org.jetbrains.haskell.psi.QVar
import org.jetbrains.haskell.scope.ExpressionScope
import org.jetbrains.haskell.psi.Expression

/**
 * Created by atsky on 4/25/14.
 */
class ValueReference(val referenceExpression: QVar) : PsiReferenceBase<QVar>(
        referenceExpression,
        TextRange(0, referenceExpression.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        return ExpressionScope(referenceExpression.getParent() as Expression).getVisibleVariables().firstOrNull {
            it.getText() == getValue()
        }
    }


    override fun getVariants(): Array<Any> = array()

}