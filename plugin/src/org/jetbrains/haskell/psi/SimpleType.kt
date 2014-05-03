package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.SomeIdReference
import org.jetbrains.haskell.psi.reference.ConstructorReference
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.parser.ElementFactory
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 10/04/14.
 */
public class SimpleType(node : ASTNode) : ASTWrapperPsiElement(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = SimpleType(node)
    }

    fun getNameElement() : Name? = findChildByClass(javaClass<Name>())



}