package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import org.jetbrains.cabal.CabalFile
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.highlight.ErrorMessage
import java.io.File
import java.io.FilenameFilter
import java.util.ArrayList

public open class Path(node: ASTNode) : ASTWrapperPsiElement(node), PropertyValue, Checkable {

    public fun getFile(): File = File(getText())

    public fun getFilename(): String = getFile().getName()

    public fun getCabalFile(): CabalFile = (getContainingFile() as CabalFile)

    public fun isAbsolute(): Boolean = getFile().isAbsolute()

    public override fun check(): List<ErrorMessage> {
        val errorMsg = when (getParent()) {
            is DataDirField         -> checkDataDir()
            is HsSourceDirsField    -> checkFromRootIf({ it -> it.isDirectory() })
            is IncludeDirsField     -> checkFromRootIf({ it -> it.isDirectory() })
            is ExtraLibDirsField    -> checkFromRootIf({ it -> it.isDirectory() })
            is RepoSubdirField      -> null
            is MainFileField        -> checkMainFile()
            is LicenseFilesField    -> checkFromRootIf({ it -> !it.isDirectory() })
            is DataFilesField       -> checkFileOrWildcard(getPathFromDataDir())
            is IncludesField        -> checkFileFromIncludes()
            is InstallIncludesField -> checkFileFromIncludes()
            else -> {
                when ((getParent() as PropertyField).getPropertyName().toLowerCase()) {
                    "extra-doc-files"    -> checkFileOrWildcard(getPathFromRoot())
                    "extra-source-files" -> checkFileOrWildcard(getPathFromRoot())
                    "extra-tmp-files"    -> checkFromRootIf({ true })
                    "c-sources"          -> checkFromRootIf({ it -> !it.isDirectory() })
                    else -> makeWarning("unchecked")
                }
            }
        }
        return if (errorMsg == null) listOf() else listOf(errorMsg)
    }

    private fun filterByWildcard(parentDir: VirtualFile): List<VirtualFile> {
        val ext = getFile().getName().replaceAll("^\\*(\\..+)$", "$1")
        return parentDir.getChildren()?.filter { it.getName().matches("^[^.]*\\Q${ext}\\E$") }  ?:  listOf()
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

    private fun makeWarning(msg: String) = ErrorMessage(this, msg, "warning")

    private fun checkDataDir(): ErrorMessage? {
        if (isAbsolute()) return makeWarning("this path should be relative")
        val file = getFileFromRoot()
        if (file == null) return makeWarning("invalid path")
        if (!file.isDirectory()) return makeWarning("this is not a directory")
        return null
    }

    private fun getPathFromDataDir(): String {
        val dataDir = getCabalFile().getDataDir()?.getFileFromRoot()
        if (dataDir == null) return getPathFromRoot()
        return getPathWithParent(dataDir)
    }

    private fun checkFileFromIncludes(): ErrorMessage? {
        val parentSection = getParentBuildSection()
        if (parentSection == null) throw IllegalStateException()
        if (findFileInDirs({ parentSection.getIncludeDirs() }) == null) return makeWarning("there is no such file")
        return null
    }

    private fun isWildcard(): Boolean = getFilename().matches("^\\*\\.(.+)$")

    public fun checkWildcard(dir: VirtualFile?): ErrorMessage? {
        if (dir == null) return makeWarning("invalid path")
        val res = filterByWildcard(dir).size
        if (res > 0) return null
        return makeWarning("no file matches this wildcard")
    }

    private fun checkMainFile(): ErrorMessage? {
        if (isAbsolute()) return makeWarning("this path should be relative")
        val parentSection = getParentBuildSection()
        if (parentSection == null) throw IllegalStateException()
        val res = findFileInDirs({ parentSection.getHSSourceDirs() })
        if (res == null)  return makeWarning("invalid path")
        val ext = res.getExtension()
        if ((ext != "hs") && (ext != "lhs")) return makeWarning("invalid extension")
        return null
    }

    public fun checkFileOrWildcard(path: String): ErrorMessage? {

        fun getParentPath(path: String) = File(path).getParent()

        val file = getAbsoluteFile(path)
        if ((file != null) && !file.isDirectory()) return null
        if (isWildcard()) return checkWildcard(getAbsoluteFile(getParentPath(path)!!))
        return makeWarning("invalid path")
    }

    private fun checkFromRootIf(isValid: (VirtualFile) -> Boolean): ErrorMessage? {
        val res = getFileFromRoot()
        if ((res == null) || !isValid(res)) return makeWarning("invalid path")
        return null
    }
}