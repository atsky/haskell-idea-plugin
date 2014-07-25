package org.jetbrains.cabal.parser

import org.jetbrains.cabal.parser.Checkable
import com.intellij.psi.PsiElement


public trait RangedValue: Checkable, PsiElement {

    public fun availibleValues(): List<String> { return listOf() }

    public override fun isValidValue(): String? {
        if (getText()!! !in availibleValues()) return "invalid field value"
        return null
    }


}