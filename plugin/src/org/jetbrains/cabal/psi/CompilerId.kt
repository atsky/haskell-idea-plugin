package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.highlight.ErrorMessage

class CompilerId(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    override fun getAvailableValues(): List<String> {
        return COMPILER_VALS
    }

    override fun check(): List<ErrorMessage> {
        if (text!!.toLowerCase() !in getAvailableValues()) return listOf(ErrorMessage(this, "invalid compiler", "error"))
        return listOf()
    }
}
