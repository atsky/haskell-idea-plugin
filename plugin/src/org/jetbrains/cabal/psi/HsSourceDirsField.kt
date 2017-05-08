package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.MultiValueField
import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.psi.Checkable
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.cabal.highlight.ErrorMessage

class HsSourceDirsField(node: ASTNode) : MultiValueField(node), PathsField, Checkable {

    override fun check(): List<ErrorMessage> {
        if (hasName("hs-source-dir")) return listOf(ErrorMessage(getKeyNode(), "The field \"hs-source-deir\" is deprecated, please, use \"hs-source-dirs\"", "warning"))
        return listOf()
    }

    override fun isValidCompletionFile(file: VirtualFile): Boolean = file.isDirectory

    override fun validVirtualFile(file: VirtualFile): Boolean = file.isDirectory
}
