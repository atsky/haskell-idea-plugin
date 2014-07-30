package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*

public class RepoKind(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    public override fun getAvailableValues(): List<String> {
        return REPO_KIND_VALS
    }
}
