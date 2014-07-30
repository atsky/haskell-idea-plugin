package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.InvalidValue

public class InvalidConditionPart(node: ASTNode) : InvalidValue(node) {

    public override fun isValidValue(): String? {
        if (getText()!!.size > 0) {
            return "invalid condition"
        }
        return null
    }
}