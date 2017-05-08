package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.highlight.ErrorMessage

class SimpleCondition(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    companion object {
        val VALID_TESTS_NAMES : List<String> = listOf(
                "os",
                "arch",
                "impl",
                "flag",
                "true",
                "false",
                "True",
                "False"
        )
    }

    override fun check(): List<ErrorMessage> {
        if (isBool()) return listOf()
        val testName = getTestName()
        if (testName != null && testName in VALID_TESTS_NAMES) {
            return listOf()
        }
        return listOf(ErrorMessage(this, "invalid test name", "error"))
    }

    fun isBool(): Boolean {
        return (children.size == 0)
    }

    fun getTestName(): String? {
        if (isBool()) return null
        return firstChild!!.text
    }
}
