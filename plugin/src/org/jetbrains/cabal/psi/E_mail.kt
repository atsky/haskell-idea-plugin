package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable

public class E_mail(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun isValidValue(): String? {
        if (!getNode().getText()!!.matches("^.+@.+\\..+$")) return "invalid identifier"
        return null
    }
}
