package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.haskell.parser.ElementFactory


public class FunctionDeclaration(node : ASTNode) : Declaration(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = FunctionDeclaration(node)
    }

    override fun getDeclarationName(): String? =
            findChildByClass(javaClass<Name>())?.getText()

}