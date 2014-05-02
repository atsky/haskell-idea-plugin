package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.ElementFactory
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.SomeIdReference

/**
 * Created by atsky on 4/25/14.
 */
public class UnparsedToken(node: ASTNode) : ASTWrapperPsiElement(node) {
    class object : ElementFactory {
        override fun create(node: ASTNode) = UnparsedToken(node)
    }
}