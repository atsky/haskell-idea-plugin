package org.jetbrains.cabal.parser

import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTDelegatePsiElement
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.parser.*
import com.intellij.psi.impl.source.tree.SharedImplUtil

public trait Section: PsiElement {
    public val REQUIRED_FIELD_NAMES: List<String>?

    public fun allRequiredFieldsExist(): String? {
        if (REQUIRED_FIELD_NAMES == null) return null

        for (fieldName in REQUIRED_FIELD_NAMES!!) {
            if (!fieldExists(fieldName)) return fieldName + " field is required"
        }
        return null
    }

    public fun fieldExists(fieldName: String): Boolean {
        val nodes = getSectChildren()
        for (node in nodes) {
            if ((node is Field) && node.hasName(fieldName)) {
                return true
            }
        }
        return false
    }

    public fun getAfterTypeInfo(): String? {
        if (getSectName() == "library") return null
        var node = getFirstChild()!!
        while (node.getNode()!!.getElementType() != CabalTokelTypes.REPO_KIND) {
            node = node.getNextSibling()!!
        }
        return node.getText()!!
    }

    public fun getSectChildren(): Array<PsiElement> {
        return (this : PsiElement).getChildren()
    }

    private fun getSectName(): String {
        return getFirstChild()!!.getText()!!
    }

    public fun getSectTypeNode(): PsiElement = getFirstChild()!!
}