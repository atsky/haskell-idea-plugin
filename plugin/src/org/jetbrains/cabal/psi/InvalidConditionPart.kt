package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.InvalidValue
import org.jetbrains.cabal.highlight.ErrorMessage

public class InvalidConditionPart(node: ASTNode) : InvalidValue(node) {

    public override fun checkValue(): List<ErrorMessage> {
        if (getText()!!.size > 0) return listOf(ErrorMessage(this, "invalid condition", "error"))
        return listOf()
    }
}