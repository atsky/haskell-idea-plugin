package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.psi.MultiValueField
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.util.ArrayList

public class InstallIncludesField(node: ASTNode) : MultiValueField(node), PathsField {

    public override fun validVirtualFile(file: VirtualFile): Boolean = !file.isDirectory()

    public override fun validRelativity(path: File): Boolean = !path.isAbsolute()

    public override fun getSourceDirs(originalRootDir: VirtualFile): List<VirtualFile>
            = (getParentBuildSection()!!.getIncludeDirs() map { it.getVirtualFile(originalRootDir) }).filterNotNull()
}