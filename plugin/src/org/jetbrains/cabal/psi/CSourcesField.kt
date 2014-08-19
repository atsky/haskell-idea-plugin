package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.cabal.psi.MultiValueField
import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.highlight.ErrorMessage
import java.io.File

public class CSourcesField(node: ASTNode) : MultiValueField(node), PathsField {

    public override fun validVirtualFile(file: VirtualFile): Boolean = !file.isDirectory()
}