package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.parser.ElementFactory

/**
 * Created by atsky on 4/11/14.
 */
public class ConstructorDeclaration(node : ASTNode) : Declaration(node), PsiNamedElement {

    class object : ElementFactory {
        override fun create(node: ASTNode) = ConstructorDeclaration(node)
    }

    override fun getName(): String? = getDeclarationName()

    override fun setName(name: String): PsiElement? {
        throw UnsupportedOperationException()
    }

    override fun getDeclarationName(): String? {
        return findChildByClass(javaClass<ConstructorName>())?.getText()
    }

}