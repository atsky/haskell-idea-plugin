package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.highlight.ErrorMessage

class Identifier(node: ASTNode) : PropertyValue(node), Checkable {

    override fun check(): List<ErrorMessage> {
        if (!node.text.matches("^[a-zA-Z](\\w|[.-])*$".toRegex())) {
            return listOf(ErrorMessage(this, "invalid identifier", "error"))
        }
        return listOf()
    }
}