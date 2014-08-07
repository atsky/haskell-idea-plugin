package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.CabalFile
import java.io.File
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.FilenameFilter
import java.util.ArrayList

public open class Path(node: ASTNode) : ASTWrapperPsiElement(node), PropertyValue {

    public fun getFile(): File = File(getText())

    public fun getFilename(): String = getFile().getName()

    public fun getCabalFile(): CabalFile = (getContainingFile() as CabalFile)

    public fun isAbsolute(): Boolean = getFile().isAbsolute()

    public fun isValidPath(): String? {
        when (getParent()) {
            is DataDirField         -> return checkDataDir()
            is HSSourceDirsField    -> return checkFromRootIf({ it -> it.isDirectory() })
            is IncludeDirsField     -> return checkFromRootIf({ it -> it.isDirectory() })
            is ExtraLibDirsField    -> return checkFromRootIf({ it -> it.isDirectory() })
            is RepoSubdirField      -> return null
            is MainFileField        -> return checkMainFile()
            is LicenseFilesField    -> return checkFromRootIf({ it -> !it.isDirectory() })
            is DataFilesField       -> return checkFileOrWildcard(getPathWithParent(getCabalFile().getActualDataDir()))
            is IncludesField        -> return checkFileFromIncludes()
            is InstallIncludesField -> return checkFileFromIncludes()
            else -> {
                when ((getParent() as PropertyField).getPropertyName().toLowerCase()) {
                    "extra-doc-files"    -> return checkFileOrWildcard(getPathFromRoot())
                    "extra-source-files" -> return checkFileOrWildcard(getPathFromRoot())
                    "extra-tmp-files"    -> return checkFromRootIf({ true })
                    "c-sources"          -> return checkFromRootIf({ it -> !it.isDirectory() })
                    else -> return "unchecked"
                }
            }
        }
    }

    private fun filterByWildcard(parentDir: VirtualFile): List<VirtualFile>? {
        val ext = getFile().getName().replaceAll("^\\*(\\..+)$", "$1")
        return parentDir.getChildren()?.filter { it.getName().matches("^[^.]*\\Q${ext}\\E$") }
    }

    private fun getCabalRootFile(): VirtualFile? = getCabalFile().getVirtualFile()?.getParent()

    private fun getFileSystem(): VirtualFileSystem? = getCabalFile().getVirtualFile()?.getFileSystem()

    public fun getFileWithParent(parent: VirtualFile): VirtualFile? {
        if (isAbsolute()) return getAbsoluteFile()
        return parent.findFileByRelativePath(getText().replace(File.separatorChar, '/'))
    }

    private fun getAbsoluteFile(): VirtualFile? = getFileSystem()?.findFileByPath(getText().replace(File.separatorChar, '/'))

    private fun getAbsoluteFile(path: String): VirtualFile? = getFileSystem()?.findFileByPath(path.replace(File.separatorChar, '/'))

    public fun getFileFromRoot(): VirtualFile? {
        if (isAbsolute()) return getAbsoluteFile()
        return getFileWithParent(getCabalRootFile()!!)
    }

    public fun getPathWithParent(parent: VirtualFile): String = if (isAbsolute()) getText() else File(parent.getPath(), getText()).getPath()

    public fun getPathFromRoot(): String = if (isAbsolute()) getText() else File(getCabalRootFile()!!.getPath(), getText()).getPath()

    private fun checkDataDir(): String? {
        if (isAbsolute()) return  "this path should be relative"
        val file = getFileFromRoot()
        if (file == null) return "invalid path"
        if (!file.isDirectory()) return "this is not a directory"
        return null
    }

    private fun findFileInDirs(getDirs: () -> List<Path>?): VirtualFile? {
        var res = getFileFromRoot()
        if ((res != null) && !res!!.isDirectory()) return res
        val dirs: List<VirtualFile?>? = getDirs()?.map({ it -> it.getFileFromRoot() })
        if (dirs == null) return null
        for (dir in dirs) {
            if (dir == null) continue
            res = getFileWithParent(dir)
            if ((res != null) && (!res!!.isDirectory())) return res
        }
        return null
    }

    public fun getParentBuildSection(): BuildSection? {
        var parent = getParent()
        while (parent != null) {
            if (parent is BuildSection) return (parent as BuildSection)
            parent = parent!!.getParent()
        }
        return null
    }

    private fun checkFileFromIncludes(): String? {
        if (findFileInDirs({ getParentBuildSection()?.getIncludeDirs() }) == null) return "there is no such file"
        return null
    }

    private fun isWildcard(): Boolean = getFilename().matches("^\\*\\.(.+)$")

    public fun checkWildcard(dir: VirtualFile?): String? {
        if (dir == null) return "invalid path"
        val res = filterByWildcard(dir)?.size
        if (res == null) return "invali path"
        if (res > 0) return null
        return "no file matches this wildcard"
    }

    private fun checkMainFile(): String? {
        if (isAbsolute()) return "this path should be relative"
        val res = findFileInDirs({ getParentBuildSection()?.getHSSourceDirs() })
        if (res == null)  return "invalid path"
        val ext = res.getExtension()
        if ((ext != "hs") && (ext != "lhs")) return "invalid extension"
        return null
    }

    public fun checkFileOrWildcard(path: String): String? {
        fun getParentPath(path: String) = File(path).getParent()

        val file = getAbsoluteFile(path)
        if ((file != null) && !file.isDirectory()) return null
        if (isWildcard()) return checkWildcard(getAbsoluteFile(getParentPath(path)!!))
        return "invalid path"
    }

    private fun checkFromRootIf(isValid: (VirtualFile) -> Boolean): String? {
        val res = getFileFromRoot()
        if ((res == null) || !isValid(res)) return "invalid path"
        return null
    }
}