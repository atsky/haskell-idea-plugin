package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.SingleValueField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile
import java.util.ArrayList
import java.io.File

class RepoSubdirField(node: ASTNode) : SingleValueField(node), PathsField {

    override fun validVirtualFile(file: VirtualFile): Boolean = true

    override fun getSourceDirs(originalRootDir: VirtualFile): List<VirtualFile> = listOf()
}