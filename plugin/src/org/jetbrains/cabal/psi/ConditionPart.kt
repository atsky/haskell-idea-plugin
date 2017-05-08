package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.highlight.ErrorMessage

class ConditionPart(node: ASTNode) : ASTWrapperPsiElement(node) {

    fun checkBrackets(): List<ErrorMessage> {
        if ((this.firstChild!!.text == "(") != (this.lastChild!!.text == ")")) {
            return listOf(ErrorMessage(this, "close bracked missing", "error", isAfterNodeError = true))
        }
        //return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<ConditionPart>()) flatMap { it.checkBrackets() }
        return listOf()
    }
}