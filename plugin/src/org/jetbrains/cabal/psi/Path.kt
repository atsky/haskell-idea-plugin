package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.CabalFile
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.highlight.ErrorMessage
import org.jetbrains.cabal.references.FilePsiReference
import java.io.File
import java.io.FilenameFilter
import java.util.ArrayList
import com.intellij.psi.PsiReference

public open class Path(node: ASTNode) : ASTWrapperPsiElement(node), PropertyValue, Checkable {

    public override fun getReference(): PsiReference? {
        val originalRootDir = getCabalRootFile()
        if (originalRootDir == null) return null
        val refFile: VirtualFile? = getVirtualFile(originalRootDir)
        if ((refFile == null) || (refFile.isDirectory())) return null
        val resolveTo = getManager()?.findFile(refFile)
        if (resolveTo == null) return null
        return FilePsiReference(this, resolveTo)
    }

    public fun getParentField(): PathsField = getParent() as PathsField

    public fun getDefaultTextRange(): TextRange = TextRange(0, getText().length())

    public fun getFile(): File = File(getText())

    public fun getFilename(): String = getFile().getName()

    public fun getCabalFile(): CabalFile = (getContainingFile() as CabalFile)

    public fun isAbsolute(): Boolean = getFile().isAbsolute()

    public fun isWildcard(): Boolean {
        val parentField = getParentField()
        if (parentField.hasName("extra-source-files") || parentField.hasName("data-files")) {
            return getFilename().matches("^\\*\\.(.+)$".toRegex())
        }
        return false
    }

    public override fun check(): List<ErrorMessage> {
        val originalRootFile = getCabalRootFile()!!
        if (isWildcard()) {
            val parentDir = getVirtualParentDir(originalRootFile)
            if (parentDir == null) return listOf(ErrorMessage(this, "invalid path", "warning"))
            if (filterByWildcard(parentDir).size() == 0) return listOf(ErrorMessage(this, "invalid wildcard", "warning"))
            return listOf()
        }
        else {
            if (getVirtualFile(originalRootFile) == null) return listOf(ErrorMessage(this, "invalid path", "warning"))
        }
        return listOf()
    }

    public fun getFileWithParent(parent: VirtualFile): VirtualFile? {
        if (isAbsolute()) return findFileByPath(parent.getFileSystem(), getText())
        return findFileByRelativePath(parent, getText())
    }

    public fun getPathWithParent(parent: VirtualFile): String = if (isAbsolute()) getText() else File(parent.getPath(), getText()).getPath()

    public fun getVirtualFile(originalRootDir: VirtualFile): VirtualFile? {
        val parentField = getParentField()
        if (!parentField.validRelativity(getFile())) return null
        for (sourceDir in parentField.getSourceDirs(originalRootDir)) {
            val file = getFileWithParent(sourceDir)
            if ((file != null) && parentField.validVirtualFile(file)) return file
        }
        return null
    }

    public fun getVirtualParentDir(originalRootDir: VirtualFile): VirtualFile? {
        val parentField = getParentField()
        if (!parentField.validRelativity(getFile())) return null
        for (sourceDir in parentField.getSourceDirs(originalRootDir)) {
            val filePath = getPathWithParent(sourceDir)
            val dirPath  = File(filePath).getParent()
            val dir = if (dirPath == null) null else findFileByPath(originalRootDir.getFileSystem(), dirPath)
            if ((dir != null) && dir.isDirectory()) return dir
        }
        return null
    }

    private fun findFileByRelativePath(parentDir: VirtualFile, path: String)
            = parentDir.findFileByRelativePath(path.replace(File.separatorChar, '/'))

    private fun findFileByPath(virtualSystem: VirtualFileSystem, path: String)
            = virtualSystem.findFileByPath(path.replace(File.separatorChar, '/'))

    private fun filterByWildcard(parentDir: VirtualFile): List<VirtualFile> {
        val ext = getFile().getName().replace("^\\*(\\..+)$".toRegex(), "$1")
        return parentDir.getChildren()?.filter { it.getName().matches("^[^.]*\\Q${ext}\\E$".toRegex()) }  ?:  listOf()
    }

    private fun getCabalRootFile(): VirtualFile? = getCabalFile().getVirtualFile()?.getParent()
}