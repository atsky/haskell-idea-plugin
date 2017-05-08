package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 11/21/14.
 */
class LetStatement(node : ASTNode) : Statement(node) {

    fun getQVar() : QVar? =
            findChildByClass(QNameExpression::class.java)?.getQVar()

    fun getValueDefinitions() : List<ValueDefinition> =
            PsiTreeUtil.getChildrenOfTypeAsList(this, ValueDefinition::class.java)
}