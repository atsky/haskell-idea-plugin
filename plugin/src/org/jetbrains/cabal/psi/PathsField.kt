package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.Path
import org.jetbrains.cabal.psi.BuildSection
import org.jetbrains.cabal.CabalFile
import java.io.File
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.FilenameFilter
import java.util.ArrayList
import com.intellij.psi.PsiFile

public trait PathsField: PropertyField {

    public fun getNextAvailableFile(prefixPath: Path, originalRootDir: VirtualFile): List<String> {
        val parentDirs = getParentDirs(prefixPath, originalRootDir)
        var res = ArrayList<String>()
        for (parentDir in parentDirs) {
            if (parentDir.isDirectory()) {
                res.addAll(parentDir.getChildren()!! filter { isValidFile(it) } map { it.getName()/*.concat(if (it.isDirectory()) "/" else "")*/ })
            }
        }
       return res
    }

    public fun isValidFile(file: VirtualFile): Boolean = true

    public fun getParentDirs(prefixPath: Path, originalRootDir: VirtualFile): List<VirtualFile> {
        val dir = getParentDirFromRoot(prefixPath, originalRootDir)
        if (dir == null) return listOf()
        return listOf(dir)
    }

    public final fun getParentDirFromRoot(prefixPath: Path, originalRootDir: VirtualFile): VirtualFile? {
        val dirPath = File(prefixPath.getPathWithParent(originalRootDir)).getParent()
        val dirFile = if (dirPath == null) null else originalRootDir.getFileSystem().findFileByPath(dirPath)
        return dirFile
    }

    public final fun getParentDirsFromSourceDirs(prefixPath: Path, originalRootDir: VirtualFile, getSourceDirs: BuildSection.() -> List<Path>?): List<VirtualFile> {
        var res = ArrayList<VirtualFile>()
        val sourceDirs = prefixPath.getParentBuildSection()?.getSourceDirs()
        if (sourceDirs == null) return res
        for (sourceDir in sourceDirs) {
            val sourceDirFile = sourceDir.getFileWithParent(originalRootDir)
            val dirPath    = if (sourceDirFile == null) null else File(prefixPath.getPathWithParent(sourceDirFile)).getParent()
            val dirFile    = if (dirPath == null)       null else originalRootDir.getFileSystem().findFileByPath(dirPath)
            if (dirFile == null) continue
            res.add(dirFile)
        }
        return res
    }
}