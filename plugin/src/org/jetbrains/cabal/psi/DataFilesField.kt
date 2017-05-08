package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.MultiValueField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class DataFilesField(node: ASTNode) : MultiValueField(node), PathsField {

    override fun validVirtualFile(file: VirtualFile): Boolean = !file.isDirectory

    override fun getSourceDirs(originalRootDir: VirtualFile): List<VirtualFile>
            = listOf(getCabalFile().getDataDir()?.getVirtualFile(originalRootDir)  ?:  originalRootDir)
}