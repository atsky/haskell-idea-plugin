package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.PropertyValue

class FullVersionConstraint(node: ASTNode) : PropertyValue(node) {

    fun getBaseName() : String = firstChild!!.text!!

    fun getConstraint() : ComplexVersionConstraint?
            = (children.firstOrNull { it is ComplexVersionConstraint }) as ComplexVersionConstraint?
}