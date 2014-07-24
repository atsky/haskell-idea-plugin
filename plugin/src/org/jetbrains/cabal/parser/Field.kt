package org.jetbrains.cabal.parser

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.SharedImplUtil

public trait Field: PsiElement {

    public fun isUniqueOnThisLevel(): Boolean {
        val siblings = (SharedImplUtil.getParent(getNode())!!).getChildren()
        var foundOne = false
        val selfType = (this : PsiElement).getNode()!!.getElementType()
        for (s in siblings) {
            if ((s.getNode()!!.getElementType() == selfType) && (s.getFirstChild()!!.getText()!!.equalsIgnoreCase(getKeyNode().getText()!!))) {
                if (!foundOne) {
                    foundOne = true
                }
                else return false
            }
        }
        return true
    }

    public fun hasName(name: String): Boolean {
        return getFirstChild()!!.getText()!!.equalsIgnoreCase(name)
    }

    public fun getToLowerCaseName(): String {
        return getFirstChild()!!.getText()!!.toLowerCase()
    }

    public fun getFieldName(): String {
        return getFirstChild()!!.getText()!!
    }

    public fun getLastValue(): String {
        return getLastChild()!!.getText()!!
    }

    public fun getKeyNode(): PsiElement = getFirstChild()!!
}
