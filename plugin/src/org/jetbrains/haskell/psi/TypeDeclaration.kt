package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.haskell.parser.ElementFactory

/**
 * Created by atsky on 4/11/14.
 */
public class TypeDeclaration(node : ASTNode) : Declaration(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = TypeDeclaration(node)
    }

    fun getNameElement() : Name? {
        val simpleType = findChildByClass(javaClass<SimpleType>())
        return simpleType?.getNameElement()
    }

    override fun getDeclarationName(): String? {
        val simpleType = findChildByClass(javaClass<SimpleType>())
        return simpleType?.getNameElement()?.getText()
    }
    
}