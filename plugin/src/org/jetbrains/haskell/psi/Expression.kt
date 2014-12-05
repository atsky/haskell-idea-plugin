package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.haskell.scope.ModuleScope

/**
 * Created by atsky on 4/25/14.
 */
public abstract class Expression(node : ASTNode) : ASTWrapperPsiElement(node) {
    abstract fun traverse(visitor : (Expression) -> Unit)
}
