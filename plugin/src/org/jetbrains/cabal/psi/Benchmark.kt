package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.Name
import java.util.ArrayList
import org.jetbrains.cabal.highlight.ErrorMessage
import java.lang.IllegalStateException

public class Benchmark(node: ASTNode) : BuildSection(node) {

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(BENCHMARK_FIELDS.keys)
        res.addAll(IF_ELSE)
        return res
    }

    public override fun check(): List<ErrorMessage> {
        val res = ArrayList<ErrorMessage>()

        val typeField   = getField(TypeField::class.java)
        val mainIsField = getField(MainFileField::class.java)

        if (typeField == null) {
            res.add(ErrorMessage(getSectTypeNode(), "type field is required", "error"))
        }
        if ((typeField?.getValue()?.getText() == "exitcode-stdio-1.0") && (mainIsField == null)) {
            res.add(ErrorMessage(getSectTypeNode(), "main-is field is required", "error"))
        }
        return res
    }

    public fun getBenchmarkName(): String {
        val res = getSectName()
        if (res == null) throw IllegalStateException()
        return res
    }
}
