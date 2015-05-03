package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.highlight.ErrorMessage

public class Identifier(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public override fun check(): List<ErrorMessage> {
        if (!getNode().getText().matches("^[a-zA-Z](\\w|[.-])*$")) {
            return listOf(ErrorMessage(this, "invalid identifier", "error"))
        }
        return listOf()
    }
}