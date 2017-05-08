package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*

class RepoKind(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    override fun getAvailableValues(): List<String> {
        return REPO_KIND_VALS
    }
}
