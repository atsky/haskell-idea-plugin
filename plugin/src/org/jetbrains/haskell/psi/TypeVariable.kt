package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.ValueReference
import org.jetbrains.haskell.psi.reference.TypeReference
import com.intellij.psi.PsiElement

/**
 * Created by atsky on 4/11/14.
 */
public class TypeVariable(node: ASTNode) : HaskellType(node) {

    fun getNameText() : String? {
        return getText()
    }

    override fun getReference(): PsiReference? {
        return TypeReference(this)
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