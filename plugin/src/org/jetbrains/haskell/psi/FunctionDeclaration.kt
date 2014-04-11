package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode


public class FunctionDeclaration(node : ASTNode) : Declaration(node) {

    override fun getDeclarationName(): String? =
            findChildByClass(javaClass<Name>())?.getText()

}