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
import com.intellij.psi.PsiFile
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.PsiManager
import org.jetbrains.haskell.fileType.HaskellFile
import org.jetbrains.haskell.parser.ElementFactory
import org.jetbrains.haskell.external.BuildWrapper
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jetbrains.cabal.CabalInterface
import org.jetbrains.cabal.CabalFileType
import org.jetbrains.cabal.CabalFile
import org.jetbrains.haskell.util.joinPath
import java.io.File
import org.jetbrains.haskell.vfs.TarGzFile
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.application.PathManager
import java.net.URL
import java.io.FileOutputStream
import java.io.IOException
import java.util.Arrays
import javax.xml.bind.JAXBElement.GlobalScope
import org.jetbrains.haskell.scope.HackageScope

/**
 * Created by atsky on 3/29/14.
 */
public class ModuleName(node: ASTNode) : ASTWrapperPsiElement(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = ModuleName(node)
    }

    override fun getReference(): PsiReference? {
        return ModuleReference(this)
    }

    public fun findModuleFile(): HaskellFile? {
        val nameToFind = getText()!!
        val module = ModuleUtilCore.findModuleForPsiElement(this);

        if (module == null) {
            return null
        }

        val sourceRoots = ModuleRootManager.getInstance(module)!!.getSourceRoots(true)

        var result: VirtualFile? = null

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
            val psiFile = PsiManager.getInstance(getProject()).findFile(result!!)
            return psiFile as HaskellFile
        }

        //val haskellFile = HackageScope.INSTANCE.getModule(this, nameToFind)
        //if (haskellFile != null) {
        //    return haskellFile
        //}

        return null
    }


    fun trace(suffix: String, dir: VirtualFile, function: (String, VirtualFile) -> Boolean): Boolean {
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
}