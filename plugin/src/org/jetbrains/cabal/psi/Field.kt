package org.jetbrains.cabal.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.CabalFile
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem

open class Field(node: ASTNode) : ASTWrapperPsiElement(node) {

    fun getType(): IElementType = node.elementType

    fun hasName(name: String): Boolean {
        return firstChild!!.text!!.equals(name, ignoreCase = true)
    }

    fun getFieldName(): String {
        return firstChild!!.text!!.toLowerCase()
    }
}
