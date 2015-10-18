package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.highlight.ErrorMessage

public class ConditionPart(node: ASTNode) : ASTWrapperPsiElement(node) {

    public fun checkBrackets(): List<ErrorMessage> {
        if ((this.getFirstChild()!!.getText() == "(") != (this.getLastChild()!!.getText() == ")")) {
            return listOf(ErrorMessage(this, "close bracked missing", "error", isAfterNodeError = true))
        }
        //return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<ConditionPart>()) flatMap { it.checkBrackets() }
        return listOf()
    }
}