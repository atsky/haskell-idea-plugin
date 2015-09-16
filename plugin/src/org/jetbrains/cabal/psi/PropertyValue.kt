package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

public open class PropertyValue(node: ASTNode) : ASTWrapperPsiElement(node) {
    public override fun getText(): String = getNode().getText()

}