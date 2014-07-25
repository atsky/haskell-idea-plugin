package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*

public class BuildType(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    public override fun availibleValues(): List<String> {
        return BUILD_TYPE_VALS
    }
}
