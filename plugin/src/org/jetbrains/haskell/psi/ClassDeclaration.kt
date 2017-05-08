package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 23/04/14.
 */
class ClassDeclaration(node : ASTNode) : ASTWrapperPsiElement(node) {

    fun getType(): HaskellType? =
            findChildByClass(HaskellType::class.java)
}