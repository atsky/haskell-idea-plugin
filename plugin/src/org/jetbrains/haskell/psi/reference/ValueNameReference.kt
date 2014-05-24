package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.ReferenceExpression
import org.jetbrains.haskell.psi.ValueName
import org.jetbrains.haskell.scope.ModuleScope

/**
 * Created by atsky on 4/25/14.
 */
class ValueNameReference(val referenceExpression: ValueName) : PsiReferenceBase<ValueName>(
        referenceExpression,
        TextRange(0, referenceExpression.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        val module = Module.findModule(getElement()!!)
        if (module == null) {
            return null
        }
        val values = ModuleScope(module).getDeclaredValues().flatMap { it.getNames() }
        return values.firstOrNull { it.getText() == getValue() }
    }


    override fun getVariants(): Array<Any> = array()

}