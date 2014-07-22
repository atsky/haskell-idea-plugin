package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable

public class BuildType(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_BUILD_TYPE : List<String> = listOf(
                "Simple",
                "Configure",
                "Custom",
                "Make"
        )
    }

    public override fun isValidValue(): String? {
        if (getNode().getText()!! !in VALID_BUILD_TYPE) return "invalid build type"
        return null
    }
}
