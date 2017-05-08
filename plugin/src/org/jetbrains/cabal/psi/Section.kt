package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTDelegatePsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.psi.util.PsiTreeUtil

import org.jetbrains.cabal.psi.PropertyField
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.highlight.ErrorMessage

import java.util.ArrayList

open class Section(node: ASTNode): Field(node), FieldContainer, Checkable {

    override fun check(): List<ErrorMessage> = listOf()

    fun getSectChildren(): List<PsiElement> = children.filter { it is Field }

    fun getSectTypeNode(): PsiElement = (children.firstOrNull { it is SectionType }) ?: throw IllegalStateException()

    fun getSectType(): String = getSectTypeNode().text!!

    protected open fun getSectName(): String? {
        var node = firstChild
        while ((node != null) && (node !is Name)) {
            node = node.nextSibling
        }
        return (node as? Name)?.text
    }
}