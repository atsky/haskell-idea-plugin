package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import java.util.ArrayList

public class IfCondition(node: ASTNode) : Section(node) {

    public override fun getRequiredFieldNames(): List<String> = listOf()

    public override fun allRequiredFieldsExist(): String? {
        if (getSectChildren().size == 0) return "empty if section is not allowed"
        return null
    }

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll((getParent()!! as Section).getAvailableFieldNames())
        res.remove("if")
        res.remove("else")
        return res
    }
}
