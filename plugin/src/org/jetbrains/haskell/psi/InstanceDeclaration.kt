package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.ElementFactory


open class InstanceDeclaration(node : ASTNode) : ASTWrapperPsiElement(node) {
    class object : ElementFactory {
        override fun create(node: ASTNode) = InstanceDeclaration(node)
    }

}