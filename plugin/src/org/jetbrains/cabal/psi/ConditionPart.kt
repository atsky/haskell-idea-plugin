package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.PropertyField
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.highlight.ErrorMessage
import com.intellij.psi.util.PsiTreeUtil
import java.util.ArrayList

public class ConditionPart(node: ASTNode) : ASTWrapperPsiElement(node) {

    public fun checkInvalidParts(): List<ErrorMessage> {
        val res = (PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<InvalidConditionPart>()) map { ErrorMessage(it, "invalid condition", "error") }) as MutableList<ErrorMessage>
        res.addAll(PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<ConditionPart>()) flatMap { it.checkInvalidParts() })
        return res
    }

    public fun checkBrackets(): List<ErrorMessage> {
        if (((this: PsiElement).getFirstChild()!!.getText() == "(") != ((this: PsiElement).getLastChild()!!.getText() == ")")) {
            return listOf(ErrorMessage(this, "close bracked missing", "error"))
        }
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<ConditionPart>()) flatMap { it.checkBrackets() }
    }
}