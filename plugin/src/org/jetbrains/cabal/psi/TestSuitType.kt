package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable

public class TestSuitType(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_TS_TYPE : List<String> = listOf(
                "exitcode-stdio-1.0",
                "detailed-1.0"
        )
    }

    public override fun isValidValue(): String? {
        if (getNode().getText()!! !in VALID_TS_TYPE) return "invalid type"
        return null
    }
}
