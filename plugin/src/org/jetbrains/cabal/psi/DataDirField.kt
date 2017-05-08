package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.SingleValueField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class DataDirField(node: ASTNode) : SingleValueField(node), PathsField {

    override fun isValidCompletionFile(file: VirtualFile): Boolean = file.isDirectory

    override fun validVirtualFile(file: VirtualFile): Boolean = file.isDirectory

    override fun validRelativity(path: File): Boolean = !path.isAbsolute
}
