package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.Section
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.cabal.parser.FIELD_TYPES
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.parser.Field

public class Benchmark(node: ASTNode) : ASTWrapperPsiElement(node), Section {
    public override val REQUIRED_FIELD_NAMES = listOf ("type")

    public override fun allRequiredFieldsExist(): String? {
        val nodes = getSectChildren()

        var typeValue: String? = null
        var mainIsFlag = false

        for (node in nodes) {
            if (node !is Field) continue
            val fieldNode: Field = node as Field
            if (fieldNode.hasName("type")) {
                typeValue = fieldNode.getLastValue()
            }
            if (fieldNode.hasName("main-is")) {
                mainIsFlag = true
            }
        }
        if (typeValue == null) return "type field is required"
        if (typeValue == "exitcode-stdio-1.0") {
            if (!mainIsFlag) return "main-is field is required"
            return null
        }
        return null
    }
}
