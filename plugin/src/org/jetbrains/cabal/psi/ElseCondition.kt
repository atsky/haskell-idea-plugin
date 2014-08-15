package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import java.util.ArrayList
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.cabal.highlight.ErrorMessage

public class ElseCondition(node: ASTNode) : Section(node) {

    public override fun check(): List<ErrorMessage> {
        if (getSectChildren().size == 0) listOf(ErrorMessage(getSectTypeNode(), "empty else section is not allowed", "error"))
        return listOf()
    }

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll((getParent() as Section).getAvailableFieldNames())
        res.remove("if")
        res.remove("else")
        return res
    }

    protected override fun getSectName(): String? = null
}
