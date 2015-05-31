package org.jetbrains.cabal.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.CabalFile
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem

public open class Field(node: ASTNode) : ASTWrapperPsiElement(node) {

    public fun getType(): IElementType = getNode().getElementType()

    public fun hasName(name: String): Boolean {
        return getFirstChild()!!.getText()!!.equals(name, ignoreCase = true)
    }

    public fun getFieldName(): String {
        return getFirstChild()!!.getText()!!.toLowerCase()
    }
}
