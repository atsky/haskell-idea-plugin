package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.highlight.ErrorMessage


interface RangedValue: Checkable, PsiElement {

    fun getAvailableValues(): List<String> { return listOf() }

    override fun check(): List<ErrorMessage> {
        if (text!! !in getAvailableValues()) return listOf(ErrorMessage(this, "invalid field value", "error"))
        return listOf()
    }
}