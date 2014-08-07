package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.DisallowedableField
import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.util.ArrayList

public class MainFileField(node: ASTNode) : PathsField(node), DisallowedableField {

    public override fun isEnabled(): String? {
        val parent = getParent()
        if (parent is TestSuite) {
            val sectType = parent.getField(javaClass<TypeField>())
            if ((sectType == null) || (sectType.getValue().getText() == "exitcode-stdio-1.0")) return null
            return "main-is field disallowed with such test suit type"
        }
        return null
    }

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