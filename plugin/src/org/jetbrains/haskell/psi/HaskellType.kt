package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement

open class HaskellType(node : ASTNode) : ASTWrapperPsiElement(node) {
    open fun getLeftTypeVariable() : TypeVariable? {
        return null
    }
}