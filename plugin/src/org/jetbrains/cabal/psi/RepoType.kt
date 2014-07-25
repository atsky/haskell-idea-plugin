package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.*

public class RepoType(node: ASTNode) : ASTWrapperPsiElement(node), RangedValue {
    public override fun availibleValues(): List<String> {
        return REPO_TYPE_VALS
    }
}
