package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.PropertyField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile

public class LicenseFilesField(node: ASTNode) : PropertyField(node), PathsField {

    public override fun isValidFile(file: VirtualFile): Boolean = !file.isDirectory()

}