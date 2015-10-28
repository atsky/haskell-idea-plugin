package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.highlight.ErrorMessage
import java.util.ArrayList
import org.jetbrains.cabal.psi.Name

/**
 * @author Evgeny.Kurbatsky
 */
public class TestSuite(node: ASTNode) : BuildSection(node) {

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(TEST_SUITE_FIELDS.keys)
        res.addAll(IF_ELSE)
        return res
    }

    public override fun check(): List<ErrorMessage> {
        val res = ArrayList<ErrorMessage>()

        val typeField    = getField(TypeField::class.java)
        val mainIsField  = getField(MainFileField::class.java)
        val testModField = getField(TestModuleField::class.java)

        if (typeField == null) {
            res.add(ErrorMessage(getSectTypeNode(), "type field is required", "error"))
        }
        if (typeField?.getValue()?.getText() == "exitcode-stdio-1.0") {
            if (mainIsField  == null) res.add(ErrorMessage(getSectTypeNode(), "main-is field is required with such test suite type", "error"))
            if (testModField != null) res.add(ErrorMessage(testModField.getKeyNode(), "test-module field is disallowed with such test suite type", "error"))
        }
        if (typeField?.getValue()?.getText() == "detailed-1.0") {
            if (mainIsField  != null) res.add(ErrorMessage(mainIsField.getKeyNode(), "main-is field is disallowed with such test suite type", "error"))
            if (testModField == null) res.add(ErrorMessage(getSectTypeNode(), "test-module field is required with such test suite type", "error"))
        }
        return res
    }

    public fun getTestSuiteName(): String {
        val res = getSectName()
        if (res == null) throw IllegalStateException()
        return res
    }
}