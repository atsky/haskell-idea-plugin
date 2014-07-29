package org.jetbrains.cabal.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.extapi.psi.ASTWrapperPsiElement

public open class Field(node: ASTNode) : ASTWrapperPsiElement(node) {

    public override fun getParent() = super<ASTWrapperPsiElement>.getParent()    //!! -> exeption O_o

    public fun getType(): IElementType = getNode().getElementType()!!

    public fun hasName(name: String): Boolean {
        return getFirstChild()!!.getText()!!.equalsIgnoreCase(name)
    }

    public fun getToLowerCaseName(): String {
        return getFirstChild()!!.getText()!!.toLowerCase()
    }

    public fun getFieldName(): String {
        return getFirstChild()!!.getText()!!
    }
}
