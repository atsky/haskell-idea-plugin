package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*

public class CompilerID(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    public override fun getAvailableValues(): List<String> {
        return COMPILER_VALS
    }

    public override fun isValidValue(): String? {
        if (getText()!!.toLowerCase() !in getAvailableValues()) return "invalid field value"
        return null
    }
}
