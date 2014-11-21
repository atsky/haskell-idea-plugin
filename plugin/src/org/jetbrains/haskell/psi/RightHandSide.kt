package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

/**
 * Created by atsky on 11/21/14.
 */
public class RightHandSide(node : ASTNode) : ASTWrapperPsiElement(node) {
    fun getWhereBindings() : WhereBindings? =
        findChildByClass(javaClass<WhereBindings>())

}