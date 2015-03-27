package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.TypeVariable
import org.jetbrains.haskell.scope.ModuleScope
import com.intellij.psi.ElementManipulator
import org.jetbrains.haskell.psi.util.HaskellElementFactory

/**
 * Created by atsky on 4/25/14.
 */
class TypeReference(val typeRef: TypeVariable) : PsiReferenceBase<TypeVariable>(
        typeRef,
        TextRange(0, typeRef.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        val module = Module.findModule(getElement()!!)
        if (module != null) {
            for (aType in ModuleScope(module).getVisibleDataDeclarations()) {
                if (aType.getDeclarationName() == typeRef.getText()) {
                    return aType.getNameElement()
                }
            }
            for (aType in ModuleScope(module).getVisibleTypeSynonyms()) {
                if (aType.getDeclarationName() == typeRef.getText()) {
                    return aType.getNameElement()
                }
            }
        }

        return null
    }


    override fun getVariants(): Array<Any> = array()


    override fun handleElementRename(newElementName: String?): PsiElement? {
        if (newElementName != null) {
            val qcon = HaskellElementFactory.createExpressionFromText(getElement().getProject(), newElementName)
            getElement().getFirstChild().replace(qcon)
            return qcon
        } else {
            return null
        }
    }
}