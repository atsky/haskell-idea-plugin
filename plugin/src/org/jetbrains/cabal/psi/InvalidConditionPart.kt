package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.InvalidValue
import org.jetbrains.cabal.highlight.ErrorMessage

public class InvalidConditionPart(node: ASTNode) : InvalidValue(node) {

}