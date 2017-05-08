package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.PropertyValue

class BuildType(node: ASTNode) : PropertyValue(node), RangedValue  {

    override fun getAvailableValues(): List<String> {
        return BUILD_TYPE_VALS
    }
}
