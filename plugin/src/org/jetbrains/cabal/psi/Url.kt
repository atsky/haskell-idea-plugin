package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.highlight.ErrorMessage

public class Url(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public override fun check(): List<ErrorMessage> {
        return listOf()
    }

}