package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.PropertyValue

public class FullVersionConstraint(node: ASTNode) : PropertyValue(node) {

    public fun getBaseName() : String = getFirstChild()!!.getText()!!

    public fun getConstraint() : ComplexVersionConstraint?
            = (getChildren() firstOrNull { it is ComplexVersionConstraint }) as ComplexVersionConstraint?
}