package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.scope.ModuleScope
import org.jetbrains.haskell.psi.QNameExpression
import org.jetbrains.haskell.scope.ExpressionScope

/**
 * Created by atsky on 4/25/14.
 */
class QNameReference(val refExpression: QNameExpression) : PsiReferenceBase<QNameExpression>(
        refExpression,
        TextRange(0, refExpression.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        val module = Module.findModule(getElement()!!)
        if (module == null) {
            return null
        }
        if (refExpression.getQVar() != null) {
            return ExpressionScope(refExpression).getVisibleSignatureDeclarations().firstOrNull {
                it.getValuesList().first?.getText() == getValue() }
        }
        if (refExpression.getQCon() != null) {
            val values = ModuleScope(module).getVisibleConstructors()
            return values.firstOrNull { it.getDeclarationName() == getValue() }
        }
        return null;
    }


    override fun getVariants(): Array<Any> = array()

}