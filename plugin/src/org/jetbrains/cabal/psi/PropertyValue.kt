package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

open class PropertyValue(node: ASTNode) : ASTWrapperPsiElement(node) {
    override fun getText(): String = node.text

}