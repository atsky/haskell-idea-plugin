package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.DisallowedableField
import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.util.ArrayList

public class MainFileField(node: ASTNode) : DisallowedableField(node), PathsField {

    //hell of a code. caution

    public override fun isEnabled(): String? {
        val parent = getParent()
        if (parent is TestSuite) {
            val sectType = parent.getField(javaClass<TypeField>())
            if ((sectType == null) || (sectType.getValue().getText() == "exitcode-stdio-1.0")) return null
            return "main-is field disallowed with such test suit type"
        }
        return null
    }

    public override fun isValidFile(file: VirtualFile): Boolean = (!file.isDirectory()) && ((file.getExtension() == "hs") || (file.getExtension() == "lhs"))

    public override fun getParentDirs(prefixPath: Path, originalFile: VirtualFile): List<VirtualFile> {
        if (prefixPath.isAbsolute()) return listOf()
        var res = ArrayList<VirtualFile>()
        res.addAll(super<PathsField>.getParentDirs(prefixPath, originalFile))
        val sourceDirs = prefixPath.getParentBuildSection()?.getHSSourceDirs()
        if (sourceDirs == null) return res
        for (sourceDir in sourceDirs) {
            val sourceFile = sourceDir.getFileWithParent(originalFile.getParent()!!)
            if (sourceFile == null) continue
            val path = File(prefixPath.getPathWithParent(sourceFile)).getParent()
            if (path == null) continue
            val newDir = originalFile.getFileSystem().findFileByPath(path)
            if (newDir == null) continue
            res.add(newDir)
        }
        return res
    }
}