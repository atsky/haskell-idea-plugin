package org.jetbrains.cabal.psi

import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTDelegatePsiElement
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.PropertyField
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.psi.util.PsiTreeUtil
import java.util.ArrayList
import org.jetbrains.cabal.highlight.ErrorMessage

public open class Section(node: ASTNode): Field(node), FieldContainer {

    public open fun checkFieldsPresence(): List<ErrorMessage> = listOf()

    public fun getSectChildren(): List<PsiElement> = getChildren() filter { it is Field }

    public fun getSectTypeNode(): PsiElement = getFirstChild()!!

    public fun getSectType(): String = getSectTypeNode().getText()!!

    protected open fun getSectName(): String? {
        var node = getFirstChild()
        while ((node != null) && (node !is Name)) {
            node = node?.getNextSibling()
        }
        return (node as? Name)?.getText()
    }
}