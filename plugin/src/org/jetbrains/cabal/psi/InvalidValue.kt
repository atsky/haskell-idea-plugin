package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.InvalidField
import org.jetbrains.cabal.highlight.ErrorMessage
import com.intellij.openapi.util.TextRange

public open class InvalidValue(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun check(): List<ErrorMessage> {
        if (getText()!! == "") {
            return listOf(ErrorMessage(this, "invalid empty value", "error", isAfterNodeError = true))
        }
        return listOf(ErrorMessage(this, "invalid value", "error"))
    }
}
