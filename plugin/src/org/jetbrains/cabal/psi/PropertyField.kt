package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.Field
import org.jetbrains.cabal.highlight.ErrorMessage
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import java.util.ArrayList
import com.intellij.psi.util.PsiTreeUtil

open class PropertyField(node: ASTNode) : Field(node) {

    fun getKeyNode(): PsiElement = firstChild!!

    fun getPropertyName(): String = getKeyNode().text!!
}
