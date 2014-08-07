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

public open class PathsField(node: ASTNode): PropertyField(node) {

    public fun getNextAvailableFile(prefixPath: Path, originalRootDir: VirtualFile): List<String> {
        val parentDirs = getParentDirs(prefixPath, originalRootDir)
        var res = ArrayList<String>()
        for (parentDir in parentDirs) {
            if (parentDir.isDirectory()) {
                res.addAll(parentDir.getChildren()!! filter { isValidFile(it) } map { it.getName().concat(if (it.isDirectory()) "/" else "") })
            }
        }
       return res
    }

    public open fun isValidFile(file: VirtualFile): Boolean = true

    public open fun getParentDirs(prefixPath: Path, originalRootDir: VirtualFile): List<VirtualFile> {
        val dirPath = File(prefixPath.getPathWithParent(originalRootDir)).getParent()
        val dirFile = if (dirPath == null) null else originalRootDir.getFileSystem().findFileByPath(dirPath)
        if (dirFile == null) return listOf()
        return listOf(dirFile)
    }
}