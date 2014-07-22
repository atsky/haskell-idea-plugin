package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable

public class CompilerID(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_COMPILER : List<String> = listOf(
                "GHC",
                "NHC",
                "YHC",
                "Hugs",
                "HBC",
                "Helium",
                "JHC",
                "LHC"
        )
    }

    public override fun isValidValue(): String? {
        if (getNode().getText()!! !in VALID_COMPILER) return "invalid compiler"
        return null
    }
}
