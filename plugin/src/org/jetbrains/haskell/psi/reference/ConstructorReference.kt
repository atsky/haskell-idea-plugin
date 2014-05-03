package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import org.jetbrains.haskell.psi.ModuleName
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.fileType.HaskellFile
import org.jetbrains.haskell.psi.Constructor

/**
 * Created by atsky on 4/11/14.
 */
class ConstructorReference(val constructor: Constructor) : PsiReferenceBase<Constructor>(
        constructor,
        TextRange(0, constructor.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        val module = Module.findModule(constructor)
        if (module != null) {
            val declarations = module.getConstructorDeclarationList().filter {
                it.getDeclarationName() == constructor.getText()
            }
            if (!declarations.empty) {
                return declarations[0];
            }

            for (import in module.getImportList()) {
                val importedModule = import.findModule()
                if (importedModule != null) {
                    for (declaration in importedModule.getConstructorDeclarationList()) {
                        if (declaration.getDeclarationName() == constructor.getText()) {
                            return declaration
                        }
                    }
                }
            }
        }

        return null;
    }


    override fun getVariants(): Array<Any> = array()


}