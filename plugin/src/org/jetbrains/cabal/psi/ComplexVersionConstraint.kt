package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.PropertyValue

public class ComplexVersionConstraint(node: ASTNode) : ASTWrapperPsiElement(node), PropertyValue {

}