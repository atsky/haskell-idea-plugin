package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.highlight.ErrorMessage

public class CompilerID(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    public override fun getAvailableValues(): List<String> {
        return COMPILER_VALS
    }

    public override fun checkValue(): List<ErrorMessage> {
        if (getText()!!.toLowerCase() !in getAvailableValues()) return listOf(ErrorMessage(this, "invalid field value", "error"))
        return listOf()
    }
}
