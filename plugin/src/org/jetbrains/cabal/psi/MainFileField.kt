package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.psi.SingleValueField
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.util.ArrayList

class MainFileField(node: ASTNode) : SingleValueField(node), PathsField {

    override fun isValidCompletionFile(file: VirtualFile): Boolean {
        if (!file.isDirectory) return (file.extension == "hs") || (file.extension == "lhs")
        for (child in file.children!!) {
            if (isValidCompletionFile(child)) return true
        }
        return false
    }

    override fun validVirtualFile(file: VirtualFile): Boolean
            = !file.isDirectory && (file.extension == "hs") || (file.extension == "lhs")

    override fun validRelativity(path: File): Boolean = !path.isAbsolute

    override fun getSourceDirs(originalRootDir: VirtualFile): List<VirtualFile> {
        var res = ArrayList<VirtualFile>()
        res.addAll(getParentBuildSection()!!.getHsSourceDirs().map({ it.getVirtualFile(originalRootDir) }).filterNotNull())
        res.add(originalRootDir)
        return res
    }
}