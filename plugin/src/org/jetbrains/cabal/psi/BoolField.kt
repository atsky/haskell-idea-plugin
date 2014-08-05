package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.PropertyField
public open class BoolField(node: ASTNode) : PropertyField(node) {
}