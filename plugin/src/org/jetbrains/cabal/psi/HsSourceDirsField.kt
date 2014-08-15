package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.PropertyField
import org.jetbrains.cabal.psi.PathsField
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.cabal.highlight.ErrorMessage

public class HsSourceDirsField(node: ASTNode) : PathsField(node) {

    public override fun checkUniqueness(): ErrorMessage? {
        if ((getParent()!!.getChildren() filter { (it is PropertyField) && (it.hasName("hs-source-dir") || it.hasName("hs-source-dirs")) }).size > 1)
                return ErrorMessage(getKeyNode(), "duplicate field", "error")
        if (hasName("hs-source-dir")) return ErrorMessage(getKeyNode(), "The field \"hs-source-deir\" is deprecated, please, use \"hs-source-dirs\"", "warning")
        return null
    }

    public override fun isValidFile(file: VirtualFile): Boolean = file.isDirectory()
}
