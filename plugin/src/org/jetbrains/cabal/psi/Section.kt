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

public open class Section(node: ASTNode): Field(node), FieldContainer, Checkable {

    public override fun check(): List<ErrorMessage> = listOf()

    public fun getSectChildren(): List<PsiElement> = getChildren() filter { it is Field }

    public fun getSectTypeNode(): PsiElement = (getChildren() firstOrNull { it is SectionType }) ?: throw IllegalStateException()

    public fun getSectType(): String = getSectTypeNode().getText()!!

    protected open fun getSectName(): String? {
        var node = getFirstChild()
        while ((node != null) && (node !is Name)) {
            node = node.getNextSibling()
        }
        return (node as? Name)?.getText()
    }
}