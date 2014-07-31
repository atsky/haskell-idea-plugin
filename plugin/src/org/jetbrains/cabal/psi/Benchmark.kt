package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

public class Benchmark(node: ASTNode) : BuildSection(node) {

    public override fun getRequiredFieldNames(): List<String> = listOf("type")

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(BENCHMARK_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }

    public override fun allRequiredFieldsExist(): String? {
        val nodes = getSectChildren()

        var typeValue: String? = null
        var mainIsFlag = false

        for (node in nodes) {
            if (node is TypeField) {
                typeValue = node.getLastValue().getText()
            }
            if (node is MainFileField) {
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
