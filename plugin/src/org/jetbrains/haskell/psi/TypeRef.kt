package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.ElementFactory
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.ValueReference
import org.jetbrains.haskell.psi.reference.TypeReference

/**
 * Created by atsky on 4/11/14.
 */
public class TypeRef(node: ASTNode) : ASTWrapperPsiElement(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = TypeRef(node)
    }

    override fun getReference(): PsiReference? {
        return TypeReference(this)
    }
}