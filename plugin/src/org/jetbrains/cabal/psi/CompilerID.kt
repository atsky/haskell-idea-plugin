package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*

public class CompilerID(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    public override fun availibleValues(): List<String> {
        return COMPILER_VALS
    }
}
