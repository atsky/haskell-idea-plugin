package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable

public class Token(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun isValidValue(): String? {
        if (!getNode().getText()!!.matches("^.+$")) return "invalid token"
        return null
    }
}
