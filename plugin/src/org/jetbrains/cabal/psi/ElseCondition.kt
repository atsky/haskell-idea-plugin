package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Section
import org.jetbrains.cabal.parser.Field

public class ElseCondition(node: ASTNode) : ASTWrapperPsiElement(node), Section {
    override public val REQUIRED_FIELD_NAMES = null

    public override fun allRequiredFieldsExist(): String? {
        if (getSectChildren().size == 0) return "empty else section is not allowed"
        return null
    }

    public override fun getAvailableFieldNames(): List<String> {
        return (getParent()!! as Section).getAvailableFieldNames()
    }
}
