package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode


abstract class Declaration(node : ASTNode) : ASTWrapperPsiElement(node) {

    abstract fun getDeclarationName() : String?

}