package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.PropertyField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.cabal.highlight.ErrorMessage

public class ExtraSourceField(node: ASTNode) : PropertyField(node), PathsField {

}