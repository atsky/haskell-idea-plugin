package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

/**
 * Created by atsky on 11/21/14.
 */
class RightHandSide(node : ASTNode) : ASTWrapperPsiElement(node) {
    fun getWhereBindings() : WhereBindings? =
        findChildByClass(WhereBindings::class.java)

    fun getLetStatement() : LetStatement? = parent?.parent as? LetStatement

}