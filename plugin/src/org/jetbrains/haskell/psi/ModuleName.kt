package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiElement
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.psi.search.FileTypeIndex
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.haskell.fileType.HaskellFileType
import org.jetbrains.haskell.psi.reference.ModuleReference

/**
 * Created by atsky on 3/29/14.
 */
public class ModuleName(node : ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReference(): PsiReference? {
        return ModuleReference(this)
    }
}