package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.PropertyValue

class TestSuiteType(node: ASTNode) : PropertyValue(node), RangedValue  {
    override fun getAvailableValues(): List<String> {
        return TS_TYPE_VALS
    }
}
