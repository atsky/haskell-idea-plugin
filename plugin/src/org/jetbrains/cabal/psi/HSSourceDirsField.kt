package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.PropertyField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile

public class HSSourceDirsField(node: ASTNode) : PathsField(node) {

    public override fun isValidFile(file: VirtualFile): Boolean = file.isDirectory()

}
