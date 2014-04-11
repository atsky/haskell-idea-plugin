package org.jetbrains.haskell.psi.reference

import org.jetbrains.haskell.psi.SomeId
import com.intellij.psi.PsiReferenceBase
import org.jetbrains.haskell.psi.ModuleName
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.fileType.HaskellFile

/**
 * Created by atsky on 4/11/14.
 */
class SomeIdReference(val someId : SomeId) : PsiReferenceBase<SomeId>(
        someId,
        TextRange(0, someId.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        val importList = Module.findModule(someId)?.getImportList() ?: listOf()

        for (import in importList) {
            val module = import.getModuleName()?.findModuleFile()?.getModule()
            if (module != null) {
                val list = module.getFunctionDeclarationList()
                for (function in list) {
                    if (function.getDeclarationName() == someId.getText()) {
                        return function
                    }
                }
            }

        }
        return null;
    }


    override fun getVariants(): Array<Any> = array()


}