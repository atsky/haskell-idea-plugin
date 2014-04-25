package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.ElementFactory

/**
 * Created by atsky on 23/04/14.
 */
class ClassDeclaration(node : ASTNode) : ASTWrapperPsiElement(node) {
    class object : ElementFactory {
        override fun create(node: ASTNode) = ClassDeclaration(node)
    }
}