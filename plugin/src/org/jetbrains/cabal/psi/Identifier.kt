package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue

public class Identifier(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public override fun isValidValue(): String? {
        if (!getNode().getText()!!.matches("^[a-zA-Z](\\w|[.-])*$")) return "invalid identifier"
        return null
    }
}