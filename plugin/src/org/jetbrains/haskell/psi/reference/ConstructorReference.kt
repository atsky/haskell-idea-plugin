package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import org.jetbrains.haskell.psi.ModuleName
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.fileType.HaskellFile
import org.jetbrains.haskell.psi.QCon
import org.jetbrains.haskell.scope.ModuleScope
import org.jetbrains.haskell.psi.ConstructorDeclaration

/**
 * Created by atsky on 4/11/14.
 */
class ConstructorReference(val constructor: QCon) : PsiReferenceBase<QCon>(
        constructor,
        TextRange(0, constructor.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        for (declaration in getConstructorsList()) {
            if (declaration.getDeclarationName() == constructor.getText()) {
                return declaration.getTypeVariable()
            }
        }

        return null;
    }

    fun getConstructorsList() : List<ConstructorDeclaration> {
        val module = Module.findModule(constructor)
        if (module != null) {
            return ModuleScope(module).getVisibleConstructors()
        }
        return listOf()
    }


    override fun getVariants(): Array<Any> {
        return getConstructorsList().copyToArray()
    }


}