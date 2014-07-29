package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.Field
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import java.util.ArrayList

public open class PropertyField(node: ASTNode) : Field(node) {

    public fun isUniqueOnThisLevel(): Boolean {
        val siblings = getParent()!!.getChildren()
        var foundOne = false
        val ownName = getFieldName()
        for (s in siblings) {
            if ((s is PropertyField) && (s.hasName(ownName))) {
                if (!foundOne) {
                    foundOne = true
                }
                else return false
            }
        }
        return true
    }

    public fun getLastValue(): PropertyValue = getLastChild()!! as PropertyValue

    public fun getValues(): List<PropertyValue> {
        var res = ArrayList<PropertyValue>()
        var nodes = getChildren()
        for (node in nodes) {
            if (node is PropertyValue) {
                res.add(node)
            }
        }
        return res
    }

    public fun getKeyNode(): PsiElement = getFirstChild()!!
}
