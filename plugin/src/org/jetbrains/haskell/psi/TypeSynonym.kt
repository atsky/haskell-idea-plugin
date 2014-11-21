package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 11/21/14.
 */
public class TypeSynonym(node : ASTNode) : Declaration(node) {

    override fun getDeclarationName(): String? {
        return getNameElement()?.getNameText()
    }

    fun getNameElement(): TypeVariable?  =
            PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<TypeVariable>()).first

}