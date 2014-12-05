package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.haskell.psi.reference.ConstructorReference

/**
 * Created by atsky on 28/11/14.
 */
public class Pattern(node : ASTNode) : ASTWrapperPsiElement(node) {
    public fun getExpression(): Expression? =
            findChildByClass(javaClass<Expression>())
}