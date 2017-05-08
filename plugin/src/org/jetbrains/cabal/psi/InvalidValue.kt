package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.InvalidField
import org.jetbrains.cabal.highlight.ErrorMessage
import com.intellij.openapi.util.TextRange

open class InvalidValue(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    override fun check(): List<ErrorMessage> {
        if (text!! == "") {
            return listOf(ErrorMessage(this, "invalid empty value", "error", isAfterNodeError = true))
        }
        return listOf(ErrorMessage(this, "invalid value", "error"))
    }
}
