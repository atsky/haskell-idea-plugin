package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import java.util.ArrayList
import org.jetbrains.cabal.highlight.ErrorMessage

class IfCondition(node: ASTNode) : Section(node) {

    override fun check(): List<ErrorMessage> {
        if (getSectChildren().size == 0) listOf(ErrorMessage(getSectTypeNode(), "empty if section is not allowed", "error"))
        return listOf()
    }

    override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll((parent as Section).getAvailableFieldNames())
        return res
    }

    override fun getSectName(): String? = null
}
