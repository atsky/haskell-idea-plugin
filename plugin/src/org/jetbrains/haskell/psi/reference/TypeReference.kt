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
        TextRange(0, typeRef.textRange!!.length)) {

    override fun resolve(): PsiElement? {
        val module = Module.findModule(element!!)
        if (module != null) {
            for (aType in ModuleScope(module).getVisibleDataDeclarations()) {
                if (aType.getDeclarationName() == typeRef.text) {
                    return aType.getNameElement()
                }
            }
            for (aType in ModuleScope(module).getVisibleTypeSynonyms()) {
                if (aType.getDeclarationName() == typeRef.text) {
                    return aType.getNameElement()
                }
            }
        }

        return null
    }


    override fun getVariants(): Array<Any> = arrayOf()


    override fun handleElementRename(newElementName: String?): PsiElement? {
        if (newElementName != null) {
            val qcon = HaskellElementFactory.createExpressionFromText(element.project, newElementName)
            element.firstChild.replace(qcon)
            return qcon
        } else {
            return null
        }
    }
}