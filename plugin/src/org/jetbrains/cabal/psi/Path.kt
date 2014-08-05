package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.CabalFile
import java.io.File
import java.io.FilenameFilter

public open class Path(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public fun isValidPath(): String? {
        when (getParent()) {
            is DataDirField      -> return checkDataDir()
            is HSSourceDirsField -> return checkFromRootDir()
            is IncludeDirsField  -> return checkFromRootDir()
            is MainFileField     -> return checkMainIs()
            is LicenseFilesField -> return checkFromRootFile()
            else -> {
                when ((getParent() as PropertyField).getPropertyName().toLowerCase()) {
                    "extra-lib-dirs"     -> return checkFromRootDir()
                    "subdir"             -> return null
                    "extra-doc-files"    -> return checkExtraFile()
                    "extra-source-files" -> return checkExtraFile()
                    "extra-tmp-files"    -> return checkFromRootPath()
                    "data-files"         -> return checkDataFile()
                    "includes"           -> return checkFileFromIncludes()
                    "install-includes"   -> return checkFileFromIncludes()
                    "c-sources"          -> return checkFromRootFile()
                    else -> return "unchecked"
                }
            }
        }
    }

    public fun filterByWildcard(wildcard: File): Array<File>? {
        val parentDir = wildcard.getParentFile()!!
        val ext = wildcard.getName().replaceAll("^\\*(\\..+)$", "$1")
        return parentDir.listFiles(object: FilenameFilter {
            override public fun accept(dir: File, name: String): Boolean {
                return (parentDir.equals(dir)) && (name.matches("^[^.]*\\Q${ext}\\E$"))
            }
        })
    }

    public fun getCabalRootPath(): String = getCabalFile().getCabalRootPath()

    public fun getCabalFile(): CabalFile = (getContainingFile() as CabalFile)

    public fun getFile(): File = File(getText())

    public fun isAbsolute(): Boolean = getFile().isAbsolute()

    public fun getFileWithParent(parentFile: File): File = File(parentFile, getText())

    public fun getFileWithParent(parentFile: String): File = getFileWithParent(File(parentFile))

    public fun getFileFromRoot(): File {
        val path = getText()
        val res = File(path)
        if (res.isAbsolute()) return res
        return File(getCabalRootPath(), path)
    }

    public fun checkFileInDirs(getDirs: () -> List<Path>?): String? {
        if (getFileFromRoot().isFile()) return null
        val dirs = getDirs()
        if (dirs == null) return "there is no such file"
        for (dir in dirs) {
            if (getFileWithParent(dir.getFileFromRoot()).isFile()) return null
        }
        return "there is no such file"
    }

    public fun checkFileFromIncludes(): String? = checkFileInDirs({ getParentBuildSection()?.getIncludeDirs() })

    public fun getParentBuildSection(): BuildSection? {
        var parent = getParent()
        while (parent != null) {
            if (parent is BuildSection) return (parent as BuildSection)
            parent = parent!!.getParent()
        }
        return null
    }

    public fun extensionIs(ext: String): Boolean {
        return getFile().getName().matches("^.+\\.\\Q${ext}\\E$")
    }

    public fun isWildcard(): Boolean {
        return getFile().getName().matches("^\\*\\.(.+)$")
    }

    public fun checkWildcard(file: File): String? {
        val res = filterByWildcard(file)?.size
        if (res == null) return "there is no such directory"
        if (res == 0)    return "no file matches this wildcard"
        return null
    }

    public fun checkDataDir(): String? {
        if (!getFileFromRoot().isDirectory()) return "there is no such directory"
        if (isAbsolute()) return "this path should be relative"
        return null
    }

    public fun checkMainIs(): String? {
        if (!extensionIs("hs") && !extensionIs("lhs")) return "invalid extension"
        if (isAbsolute()) return "this path should be relative"
        return checkFileInDirs({ getParentBuildSection()?.getHSSourceDirs() })
    }

    public fun checkDataFile(): String? {
        val file = getFileWithParent(getCabalFile().getActualDataDir())
        if (file.isFile()) return null
        if (isWildcard()) return checkWildcard(file)
        return "there is no such file"
    }

    public fun checkExtraFile(): String? {
        val file = getFileFromRoot()
        if (file.isFile()) return null
        if (isWildcard()) return checkWildcard(file)
        return "there is no such file"
    }

    public fun checkFromRootDir (): String? = if (getFileFromRoot().isDirectory()) null else "there is no such directory"

    public fun checkFromRootFile(): String? = if (getFileFromRoot().isFile())      null else "there is no such file"

    public fun checkFromRootPath(): String? = if (getFileFromRoot().exists())      null else "there is no such path"
}