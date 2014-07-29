package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue

/**
 * Created by atsky on 13/12/13.
 */
public class Name(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public override fun isValidValue(): String? {
        if (!getNode().getText()!!.matches("^([a-zA-Z0-9]+-)*[a-zA-Z0-9]+$")) return "invalid name"
        return null
    }
}