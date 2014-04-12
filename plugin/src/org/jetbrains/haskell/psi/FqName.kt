package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.ElementFactory

/**
 * Created by atsky on 3/14/14.
 */
open class FqName(node : ASTNode) : ASTWrapperPsiElement(node) {
    class object : ElementFactory {
        override fun create(node: ASTNode) = FqName(node)
    }

}