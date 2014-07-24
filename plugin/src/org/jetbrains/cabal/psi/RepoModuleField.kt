package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Field
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.parser.CanBeDisabledField
import org.jetbrains.cabal.parser.Section

public class RepoModuleField(node: ASTNode) : ASTWrapperPsiElement(node), CanBeDisabledField, Field {

    public override fun isEnabled(): String? {
        val parent = (this : PsiElement).getParent()!! as SourceRepo
        val repoType = parent.getRepoType()
        if ((repoType == null) || (repoType == "cvs")) return null
        return "module field schould only be used with cvs repository type"
    }
}
