package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue

public class Option(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public override fun isValidValue(): String? {
        if (!getNode().getText()!!.matches("^(\\w|[.\\-@])*$")) return "invalid option"
        return null
    }
}
