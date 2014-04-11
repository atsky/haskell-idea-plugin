package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode


public abstract class Declaration(node : ASTNode) : ASTWrapperPsiElement(node) {

    public abstract fun getDeclarationName() : String?

}