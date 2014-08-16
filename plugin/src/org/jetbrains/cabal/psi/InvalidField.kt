package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.Field
import org.jetbrains.cabal.highlight.ErrorMessage

public class InvalidField(node: ASTNode) : Field(node), Checkable {

    public override fun check(): List<ErrorMessage> {
        return listOf(ErrorMessage(this, "invalid field", "error"))
    }
}
