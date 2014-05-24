package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.haskell.parser.ElementFactory
import com.intellij.psi.util.PsiTreeUtil


public class ValueBody(node : ASTNode) : ASTWrapperPsiElement(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = ValueBody(node)
    }

}