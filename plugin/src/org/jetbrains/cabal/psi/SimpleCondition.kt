package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable

public class SimpleCondition(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_TESTS_NAMES : List<String> = listOf(
                "os",
                "arch",
                "impl",
                "flag"
        )
    }

    public override fun isValidValue(): String? {
        if (isBool()) return null
        if (getTestName() in VALID_TESTS_NAMES) {
            return null
        }
        return "invalid test name"
    }

    public fun isBool(): Boolean {
        return (getChildren().size == 0)
    }

    public fun getTestName(): String? {
        if (isBool()) return null
        return getFirstChild()!!.getText()
    }
}
