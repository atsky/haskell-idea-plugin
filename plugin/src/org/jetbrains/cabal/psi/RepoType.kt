package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable

public class RepoType(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_REPO_TYPE : List<String> = listOf(
                "darcs",
                "git",
                "svn",
                "cvs",
                "mercurial",
                "hg",
                "bazaar",
                "bzr",
                "arch",
                "monotone"
        )
    }

    public override fun isValidValue(): String? {
        if (getNode().getText()!! !in VALID_REPO_TYPE) return "invalid type"
        return null
    }
}
