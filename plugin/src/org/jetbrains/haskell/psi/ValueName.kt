package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.ElementFactory
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.ValueNameReference

/**
 * Created by atsky on 4/11/14.
 */
public class ValueName(node: ASTNode) : ASTWrapperPsiElement(node), PsiNamedElement {

    class object : ElementFactory {
        override fun create(node: ASTNode) = ValueName(node)
    }

    override fun getName(): String? {
        return getText()
    }

    override fun setName(name: String): PsiElement? {
        throw UnsupportedOperationException()
    }


    override fun getReference(): PsiReference? {
        if (getParent() is ValueBody) {
            return ValueNameReference(this)
        }
        return null
    }
}