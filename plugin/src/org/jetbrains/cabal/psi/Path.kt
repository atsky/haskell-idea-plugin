package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.CabalFile
import java.io.File

public open class Path(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public fun isValidPath(): Boolean {
        when ((getParent()!! as PropertyField).getPropertyName().toLowerCase()) {
            "data-dir"       -> return checkDataDir()
            "include-dirs"   -> return getFile().exists()
            "extra-lib-dirs" -> return getFile().exists()
            "subdir"         -> return true
            "main-is"        -> return checkMainIs()
            else       -> return getFile().exists()
        }
    }

    public fun getCabalRootPath(): String? = (getContainingFile() as CabalFile?)?.getCabalRootPath()

    public fun getExactFile(): File = File(getText())

    public fun getFile(): File {
        val path = getText()
        val res = File(path)
        if (res.isAbsolute()) return res
        return File(getCabalRootPath(), path)
    }

    public fun extentionIs(ext: String): Boolean {
        return getExactFile().getName().matches(".+\\.${ext}$")
    }

    public fun getFileWithParent(parentFile: File): File = File(parentFile, getText())

    public fun checkDataDir(): Boolean = File(getText()).isAbsolute() && getFile().isDirectory()

    public fun getParentBuildSection(): BuildSection? {
        var parent = getParent()
        while (parent != null) {
            if (parent is BuildSection) return (parent as BuildSection)
            parent = parent!!.getParent()
        }
        return null
    }

    public fun checkMainIs(): Boolean {
        if (!extentionIs("hs") && !extentionIs("lhs")) return false
        if (getFile().isFile()) return true
        val dirs = getParentBuildSection()?.getHSSourceDirs()
        if (dirs == null) return false
        for (dir in dirs) {
            if (getFileWithParent(dir.getFile()).isFile()) return true
        }
        return false
    }
}