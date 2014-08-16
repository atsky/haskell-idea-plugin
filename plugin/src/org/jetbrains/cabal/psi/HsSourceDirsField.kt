package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.PropertyField
import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.psi.Checkable
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.cabal.highlight.ErrorMessage

public class HsSourceDirsField(node: ASTNode) : PropertyField(node), PathsField, Checkable {

    public override fun check(): List<ErrorMessage> {
        if (hasName("hs-source-dir")) return listOf(ErrorMessage(getKeyNode(), "The field \"hs-source-deir\" is deprecated, please, use \"hs-source-dirs\"", "warning"))
        return listOf()
    }

    public override fun isValidFile(file: VirtualFile): Boolean = file.isDirectory()
}
