package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.PropertyValue

public class BenchmarkType(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue, PropertyValue {
    public override fun getAvailableValues(): List<String> {
        return BENCH_TYPE_VALS
    }
}
