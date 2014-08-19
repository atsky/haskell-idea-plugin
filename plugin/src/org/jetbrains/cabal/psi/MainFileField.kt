package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.psi.SingleValueField
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.util.ArrayList

public class MainFileField(node: ASTNode) : SingleValueField(node), PathsField {

    public override fun isValidCompletionFile(file: VirtualFile): Boolean {
        if (!file.isDirectory()) return (file.getExtension() == "hs") || (file.getExtension() == "lhs")
        for (child in file.getChildren()!!) {
            if (isValidCompletionFile(child)) return true
        }
        return false
    }

    public override fun validVirtualFile(file: VirtualFile): Boolean
            = !file.isDirectory() && (file.getExtension() == "hs") || (file.getExtension() == "lhs")

    public override fun validRelativity(path: File): Boolean = !path.isAbsolute()

    public override fun getSourceDirs(originalRootDir: VirtualFile): List<VirtualFile> {
        var res = ArrayList<VirtualFile>()
        res.addAll((getParentBuildSection()!!.getHsSourceDirs() map { it.getVirtualFile(originalRootDir) }).filterNotNull())
        res.add(originalRootDir)
        return res
    }
}