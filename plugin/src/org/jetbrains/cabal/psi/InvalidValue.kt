package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
//import org.jetbrains.cabal.psi.InvalidProperty

public class InvalidValue(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun isValidValue(): String? {
        if (getParent() is InvalidProperty) {
            return null
        }
        return "invalid value"
    }
}
