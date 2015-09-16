package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiFile
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.Path
import org.jetbrains.cabal.psi.BuildSection
import org.jetbrains.cabal.CabalFile
import java.io.FilenameFilter
import java.io.File
import java.util.ArrayList

public interface PathsField: PsiElement {

    public fun isValidCompletionFile(file: VirtualFile): Boolean = true

    public fun validVirtualFile(file: VirtualFile): Boolean

    public fun validRelativity(path: File): Boolean = true

    public final fun getCabalFile(): CabalFile = (getContainingFile() as CabalFile)

    public fun getParentBuildSection(): BuildSection? {
        var parent = getParent()
        while (parent != null) {
            if (parent is BuildSection) return parent
            parent = parent.getParent()
        }
        return null
    }

    public fun getSourceDirs(originalRootDir: VirtualFile): List<VirtualFile> = listOf(originalRootDir)


    public final fun getParentDirs(prefixPath: Path, originalRootDir: VirtualFile): List<VirtualFile> {

        fun getParentPathFrom(sourceDir: VirtualFile) = File(prefixPath.getPathWithParent(sourceDir)).getParent()

        fun findFileByPath(path: String) = originalRootDir.getFileSystem().findFileByPath(path.replace(File.separatorChar, '/'))

        if (!validRelativity(prefixPath.getFile())) return listOf()
        val parentPaths = (getSourceDirs(originalRootDir) map { getParentPathFrom(it) }).filterNotNull()
        return (parentPaths map { findFileByPath(it) }).filterNotNull() filter { it.isDirectory() }
    }

    public final fun getNextAvailableFile(prefixPath: Path, originalRootDir: VirtualFile): List<String> {
        val parentDirs = getParentDirs(prefixPath, originalRootDir)
        val completionFiles = ArrayList<VirtualFile>()
        for (dir in parentDirs) {
            completionFiles.addAll(dir.getChildren()!! filter { isValidCompletionFile(it) })
        }
        return completionFiles map { it.getName().concat(if (it.isDirectory()) "/" else "") }
    }
}