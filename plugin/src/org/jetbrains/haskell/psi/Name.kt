package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.ElementFactory
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiElement

/**
 * Created by atsky on 4/11/14.
 */
public class Name(node: ASTNode) : ASTWrapperPsiElement(node), PsiNamedElement {

    class object : ElementFactory {
        override fun create(node: ASTNode) = Name(node)
    }

    override fun getName(): String? {
        return getText()
    }

    override fun setName(name: String): PsiElement? {
        throw UnsupportedOperationException()
    }


}