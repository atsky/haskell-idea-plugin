package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.InvalidProperty
import org.jetbrains.cabal.highlight.ErrorMessage

public open class InvalidValue(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun checkValue(): List<ErrorMessage> {
        if (getParent() is InvalidProperty) {
            return listOf()
        }
        return listOf(ErrorMessage(this, "invalid value", "error"))
    }
}
