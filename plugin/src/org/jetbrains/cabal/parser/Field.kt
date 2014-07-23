package org.jetbrains.cabal.parser

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.SharedImplUtil

public trait Field: PsiElement{

    fun isUniqueOnThisLevel(): Boolean {
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

    fun getKeyNode(): PsiElement = getFirstChild()!!
}
