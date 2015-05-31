package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.highlight.ErrorMessage


public interface RangedValue: Checkable, PsiElement {

    public fun getAvailableValues(): List<String> { return listOf() }

    public override fun check(): List<ErrorMessage> {
        if (getText()!! !in getAvailableValues()) return listOf(ErrorMessage(this, "invalid field value", "error"))
        return listOf()
    }
}