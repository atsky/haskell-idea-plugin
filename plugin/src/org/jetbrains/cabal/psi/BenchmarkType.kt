package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable

public class BenchmarkType(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_BM_TYPE : List<String> = listOf(
                "exitcode-stdio-1.0"
        )
    }

    public override fun isValidValue(): String? {
        if (getNode().getText()!! !in VALID_BM_TYPE) return "invalid type"
        return null
    }
}
