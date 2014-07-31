package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

/**
 * @author Evgeny.Kurbatsky
 */
public class TestSuite(node: ASTNode) : BuildSection(node) {

    public override fun getRequiredFieldNames(): List<String> = listOf("type")

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(TEST_SUITE_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }

    public override fun allRequiredFieldsExist(): String? {
        val nodes = getSectChildren()

        var typeValue: PropertyValue? = null
        var mainIsFlag = false
        var testModFlag = false

        for (node in nodes) {
            when (node) {
                is TypeField       -> typeValue = node.getLastValue()
                is MainFileField   -> mainIsFlag = true
                is TestModuleField -> testModFlag = true
            }
        }
        if (typeValue == null) return "type field is required"
        if (typeValue!!.getText() == "exitcode-stdio-1.0") {
            if (!mainIsFlag) return "main-is field is required"
            return null
        }
        if (typeValue!!.getText() == "detailed-1.0") {
            if (!testModFlag) return "test-module field is required"
            return null
        }
        return null
    }
}