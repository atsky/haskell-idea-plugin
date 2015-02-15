package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 11/21/14.
 */
public class LetStatement(node : ASTNode) : Statement(node) {

    fun getQVar() : QVar? =
            findChildByClass(javaClass<QNameExpression>())?.getQVar()

    fun getValueDefinitions() : List<ValueDefinition> =
            PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
}