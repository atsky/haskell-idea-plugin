package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.highlight.ErrorMessage

public class Language(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    public override fun getAvailableValues(): List<String> {
        return LANGUAGE_VALS
    }
}
