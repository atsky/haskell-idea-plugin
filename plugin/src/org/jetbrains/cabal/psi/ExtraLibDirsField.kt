package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.MultiValueField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile

public class ExtraLibDirsField(node: ASTNode) : MultiValueField(node), PathsField {

    public override fun validVirtualFile(file: VirtualFile): Boolean = file.isDirectory()

    public override fun isValidCompletionFile(file: VirtualFile): Boolean = file.isDirectory()
}