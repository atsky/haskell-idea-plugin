package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*

public class Flag(node: ASTNode) : ASTWrapperPsiElement(node), Section {
    public override val REQUIRED_FIELD_NAMES: List<String>? = null

    public override fun getAvailableFieldNames(): List<String> {
        return FLAG_FIELDS
    }
}
