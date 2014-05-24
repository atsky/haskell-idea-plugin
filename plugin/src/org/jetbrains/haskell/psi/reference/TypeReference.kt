package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.ReferenceExpression
import org.jetbrains.haskell.psi.TypeRef
import org.jetbrains.haskell.scope.ModuleScope

/**
 * Created by atsky on 4/25/14.
 */
class TypeReference(val typeRef: TypeRef) : PsiReferenceBase<TypeRef>(
        typeRef,
        TextRange(0, typeRef.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        val module = Module.findModule(getElement()!!)
        if (module != null) {
            for (aType in  ModuleScope(module).getVisibleTypes()) {
                if (aType.getName() == typeRef.getText()) {
                    return aType
                }
            }
        }

        return null
    }


    override fun getVariants(): Array<Any> = array()


}