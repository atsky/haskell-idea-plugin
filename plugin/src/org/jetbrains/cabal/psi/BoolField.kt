package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable

public class BoolField(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {
}