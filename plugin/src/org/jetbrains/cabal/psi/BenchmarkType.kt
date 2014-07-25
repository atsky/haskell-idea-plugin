package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*

public class BenchmarkType(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    public override fun availibleValues(): List<String> {
        return BENCH_TYPE_VALS
    }
}
