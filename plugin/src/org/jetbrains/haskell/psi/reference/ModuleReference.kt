package org.jetbrains.haskell.psi.reference

import com.intellij.psi.PsiReferenceBase
import org.jetbrains.haskell.psi.ModuleName
import com.intellij.psi.PsiElement
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.psi.search.FileTypeIndex
import org.jetbrains.haskell.fileType.HaskellFileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiFile

/**
 * Created by atsky on 4/4/14.
 */

class ModuleReference(moduleName : ModuleName) : PsiReferenceBase<ModuleName>(
        moduleName,
        TextRange(0, moduleName.getTextRange()!!.getLength())) {

    override fun resolve(): PsiFile? {
        return getElement()!!.findModuleFile()
    }

    override fun getVariants(): Array<Any> = arrayOf()
}