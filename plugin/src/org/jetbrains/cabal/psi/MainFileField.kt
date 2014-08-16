package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.psi.SingleValueField
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.util.ArrayList

public class MainFileField(node: ASTNode) : SingleValueField(node), PathsField {

    public override fun isValidFile(file: VirtualFile): Boolean {
        if (!file.isDirectory()) return (file.getExtension() == "hs") || (file.getExtension() == "lhs")
        for (child in file.getChildren()!!) {
            if (isValidFile(child)) return true
        }
        return false
    }

    public override fun getParentDirs(prefixPath: Path, originalRootDir: VirtualFile): List<VirtualFile> {
        if (prefixPath.isAbsolute()) return listOf()
        var res = ArrayList<VirtualFile>()

        val fromRootDir = getParentDirFromRoot(prefixPath, originalRootDir)
        if (fromRootDir != null) res.add(fromRootDir)
        res.addAll(getParentDirsFromSourceDirs(prefixPath, originalRootDir, { getHSSourceDirs() }))

        return res
    }
}