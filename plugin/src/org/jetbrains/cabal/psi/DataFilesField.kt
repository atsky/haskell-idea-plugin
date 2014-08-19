package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.MultiValueField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

public class DataFilesField(node: ASTNode) : MultiValueField(node), PathsField {

    public override fun validVirtualFile(file: VirtualFile): Boolean = !file.isDirectory()

    public override fun getSourceDirs(originalRootDir: VirtualFile): List<VirtualFile>
            = listOf(getCabalFile().getDataDir()?.getVirtualFile(originalRootDir)  ?:  originalRootDir)
}