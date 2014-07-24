package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.Section
import org.jetbrains.cabal.parser.Field

/**
 * @author Evgeny.Kurbatsky
 */
public class TestSuite(node: ASTNode) : ASTWrapperPsiElement(node), Section {
    public override val REQUIRED_FIELD_NAMES = listOf ("type")

    public override fun allRequiredFieldsExist(): String? {
        val nodes = getSectChildren()

        var typeValue: String? = null
        var mainIsFlag = false
        var testModFlag = false

        for (node in nodes) {
            if (node !is Field) continue
            when ((node as Field).getFieldName()) {
                "type" -> typeValue = node.getLastValue()
                "main-is" -> mainIsFlag = true
                "test-module" -> testModFlag = true
            }
        }
        if (typeValue == null) return "type field is required"
        if (typeValue == "exitcode-stdio-1.0") {
            if (!mainIsFlag) return "main-is field is required"
            return null
        }
        if (typeValue == "detailed-1.0") {
            if (!testModFlag) return "test-module field is required"
            return null
        }
        return null
    }
}