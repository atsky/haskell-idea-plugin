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

/**
 * Created by atsky on 4/4/14.
 */

class ModuleReference(moduleName : ModuleName) : PsiReferenceBase<ModuleName>(
        moduleName,
        TextRange(0, moduleName.getTextRange()!!.getLength())) {

    override fun resolve(): PsiElement? {
        val nameToFind = myElement!!.getText()

        val module = ModuleUtilCore.findModuleForPsiElement(myElement)!!;

        val sourceRoots = ModuleRootManager.getInstance(module)!!.getSourceRoots(true)

        var result : VirtualFile? = null

        for (root in sourceRoots) {
            trace("", root) { name, file ->
                if (nameToFind == name) {
                    result = file
                    false
                } else {
                    true
                }
            }
        }

        if (result != null) {
            val psiFile = PsiManager.getInstance(myElement.getProject()).findFile(result!!)
            return psiFile
        }
        return null
    }


    fun trace(suffix : String, dir : VirtualFile, function : (String, VirtualFile) -> Boolean) : Boolean {
        for (file in dir.getChildren()!!) {
            if (file.isDirectory()) {
                if (!trace(suffix + file.getName() + ".", file, function)) {
                    return false;
                }
            } else {
                if (file.getFileType() == HaskellFileType.INSTANCE) {
                    if (!function(suffix + file.getNameWithoutExtension(), file)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    override fun getVariants(): Array<Any> = array()
}