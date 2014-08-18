package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.MultiValueField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile

public class IncludeDirsField(node: ASTNode) : MultiValueField(node), PathsField {

    public override fun isValidFile(file: VirtualFile): Boolean = file.isDirectory()
}