package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable

public class Condition(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_TESTS_NAMES : List<String> = listOf(
                "os",
                "arch",
                "impl"
        )
    }

    public override fun isValidValue(): String? {
        if (getChildren().size != 1) {
            if (getFirstChild()!!.getText() in VALID_TESTS_NAMES) {
                return null
            }
            else {
                return null                                                 //TODO: check whether there is such flag
            }
        }
        return null
    }
}
