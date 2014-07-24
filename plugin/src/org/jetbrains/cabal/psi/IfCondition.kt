package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Section

public class IfCondition(node: ASTNode) : ASTWrapperPsiElement(node), Section {
    public override val REQUIRED_FIELD_NAMES = null

    public override fun allRequiredFieldsExist(): String? {
        if (getSectChildren().size == 0) return "empty if section is not allowed"
        return null
    }
}
