package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.ValueReference
import org.jetbrains.haskell.psi.reference.TypeReference
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import org.jetbrains.haskell.psi.util.HaskellElementFactory

/**
 * Created by atsky on 4/11/14.
 */
public class TypeVariable(node: ASTNode) : HaskellType(node), PsiNamedElement {

    override fun getName(): String? {
        return getText()
    }

    override fun getLeftTypeVariable() : TypeVariable? {
        return this;
    }

    override fun setName(name: String): PsiElement? {
        val qcon = HaskellElementFactory.createExpressionFromText(getProject(), name)
        getFirstChild().replace(qcon)
        return qcon
    }

    fun getNameText() : String? {
        return getText()
    }

    override fun getReference(): PsiReference? =
        if (!isConstructor()) {
            TypeReference(this)
        } else {
            null
        }


    fun isConstructor() : Boolean {
        var current : PsiElement? = this
        while (true) {
            val parent = current!!.getParent()
            if (parent is ConstructorDeclaration) {
                return true
            }
            if (parent !is ApplicationType || parent.getChildrenTypes()[0] != current) {
                return false
            }
            current = parent
        }
    }
}