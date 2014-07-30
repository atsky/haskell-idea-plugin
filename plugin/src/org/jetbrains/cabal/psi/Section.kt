package org.jetbrains.cabal.psi

import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTDelegatePsiElement
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.PropertyField
import com.intellij.psi.impl.source.tree.SharedImplUtil
import java.util.ArrayList

public open class Section(node: ASTNode): Field(node) {

    public open fun getRequiredFieldNames(): List<String> = listOf()

    public open fun getAvailableFieldNames(): List<String> = listOf()

    public open fun allRequiredFieldsExist(): String? {
        for (fieldName in getRequiredFieldNames()) {
            if (!fieldExists(fieldName)) return fieldName + " field is required"
        }
        return null
    }

    public fun fieldExists(fieldName: String): Boolean {
        val nodes = getSectChildren()
        for (node in nodes) {
            if ((node is PropertyField) && node.hasName(fieldName)) {
                return true
            }
            if ((node is Section) && node.fieldExists(fieldName)) {
                return true
            }
        }
        return false
    }

    public fun getField(fieldType: IElementType): PropertyField? {
        var nodes = getSectChildren()
        for (node in nodes) {
            if ((node is PropertyField) && (node.getType() == fieldType)) {
                return node
            }
        }
        return null
    }

    public fun getAfterTypeNode(): PsiElement? {
        if (this is Library) return null
        var node = getFirstChild()!!
        while ((node !is RepoKind) && (node !is Name)) {
            node = node.getNextSibling()!!
        }
        return node
    }

    public fun getSectChildren(): ArrayList<PsiElement> {
        var res = ArrayList<PsiElement>()
        var nodes = getChildren()
        for (node in nodes) {
            if (node is Field) {
                res.add(node)
            }
        }
        return res
    }

    public fun getSectTypeNode(): PsiElement = getFirstChild()!!
}