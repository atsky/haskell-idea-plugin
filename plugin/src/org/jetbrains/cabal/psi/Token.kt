package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.highlight.ErrorMessage

public class Token(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public override fun check(): List<ErrorMessage> {
        if (!getNode().getText()!!.matches("^.+$")) return listOf(ErrorMessage(this, "invalid token", "error"))
        return listOf()
    }
}
