package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.PropertyValue

public class FullVersionConstraint(node: ASTNode) : ASTWrapperPsiElement(node), PropertyValue {

    public fun getBaseName() : String = getFirstChild()!!.getText()!!

    public fun getConstraint() : ComplexVersionConstraint? {
        var nodes = getChildren()
        for (node in nodes) {
            if (node is ComplexVersionConstraint) {
                return node
            }
        }
        return null
    }
}