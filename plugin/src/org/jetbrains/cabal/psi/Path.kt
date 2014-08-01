package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.CabalFile
import java.io.File

public open class Path(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public fun isValidPath(): String? {
        when ((getParent()!! as PropertyField).getPropertyName().toLowerCase()) {
            "data-dir"           -> return checkDataDir()
            "include-dirs"       -> return checkFromRootDir()
            "extra-lib-dirs"     -> return checkFromRootDir()
            "subdir"             -> return null
            "main-is"            -> return checkMainIs()
            "extra-doc-files"    -> return checkFromRootFile()
            "extra-source-files" -> return checkFromRootFile()
            "extra-tmp-files"    -> return checkFromRootPath()
            "data-files"         -> return checkDataFiles()
            "includes"           -> return checkFileFromIncludes()
            "install-includes"   -> return checkFileFromIncludes()
            else       -> return "unchecked"
        }
    }

    public fun getCabalRootPath(): String = getCabalFile().getCabalRootPath()

    public fun getFile(): File = File(getText())

    public fun getFileFromRoot(): File {
        val path = getText()
        val res = File(path)
        if (res.isAbsolute()) return res
        return File(getCabalRootPath(), path)
    }

    public fun getFileWithParent(parentFile: File): File = File(parentFile, getText())

    public fun getFileWithParent(parentFile: String): File = getFileWithParent(File(parentFile))

    public fun getParentBuildSection(): BuildSection? {
        var parent = getParent()
        while (parent != null) {
            if (parent is BuildSection) return (parent as BuildSection)
            parent = parent!!.getParent()
        }
        return null
    }

    public fun getCabalFile(): CabalFile = (getContainingFile() as CabalFile)

    public fun extensionIs(ext: String): Boolean {
        return getFile().getName().matches(".+\\.${ext}$")
    }

    public fun checkDataDir(): String? {
        if (!getFileFromRoot().isDirectory()) return "there is no such directory"
        if (File(getText()).isAbsolute()) return "this directory should be absolute"
        return null
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

    public fun checkMainIs(): String? {
        if (!extensionIs("hs") && !extensionIs("lhs")) return "invalid extension"
        return checkFileInDirs({ getParentBuildSection()?.getHSSourceDirs() })
    }

    public fun checkLicense(): String? {
        if (getFileFromRoot().isFile()) return null
        return "there is no such file"
    }

    public fun checkDataFiles(): String? {
        if (getFileWithParent(getCabalFile().getActualDataDir()).isFile()) return null
        return "there is no such file"
    }

    public fun checkFromRootDir (): String? = if (getFileFromRoot().isDirectory()) null else "there is no such directory"

    public fun checkFromRootFile(): String? = if (getFileFromRoot().isFile())      null else "there is no such file"

    public fun checkFromRootPath(): String? = if (getFileFromRoot().exists())      null else "there is no such path"
}