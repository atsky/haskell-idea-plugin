package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable

public class RepoKind(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_REPO_KIND : List<String> = listOf(
                "this",
                "head"
        )
    }

    public override fun isValidValue(): String? {
        if (getNode().getText()!! !in VALID_REPO_KIND) return "invalid repository source kind"
        return null
    }
}
