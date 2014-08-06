package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.Path
import org.jetbrains.cabal.CabalFile
import java.io.File
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.FilenameFilter
import java.util.ArrayList
import com.intellij.psi.PsiFile

public trait PathsField {

    public fun getNextAvailableFile(prefixPath: Path, originalFile: VirtualFile): List<String> {
        val parentDirs = getParentDirs(prefixPath, originalFile)
        var res = ArrayList<String>()
        for (parentDir in parentDirs) {
            if (parentDir.isDirectory()) {
                res.addAll(parentDir.getChildren()!! filter { isValidFile(it) } map { it.getName() })
            }
        }
       return res
    }

    public fun isValidFile(file: VirtualFile): Boolean = true

    public fun getParentDirs(prefixPath: Path, originalFile: VirtualFile): List<VirtualFile> {
        val path = File(prefixPath.getPathWithParent(originalFile.getParent()!!)).getParent()
        if (path == null) return listOf()
        val dir = originalFile.getFileSystem().findFileByPath(path)
        if (dir == null) return listOf()
        return listOf(dir)
    }
}