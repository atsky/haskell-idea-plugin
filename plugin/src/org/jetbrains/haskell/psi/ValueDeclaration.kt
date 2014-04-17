package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.haskell.parser.ElementFactory


public class ValueDeclaration(node : ASTNode) : Declaration(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = ValueDeclaration(node)
    }

    override fun getDeclarationName(): String? =
            findChildByClass(javaClass<Name>())?.getText()

}