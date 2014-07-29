package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.DisallowedableField
import org.jetbrains.cabal.parser.CabalTokelTypes

public class RepoModuleField(node: ASTNode) : DisallowedableField(node) {

    public override fun isEnabled(): String? {
        val parent = getParent() as SourceRepo
        val typeField = parent.getField(CabalTokelTypes.TYPE)
        if ((typeField == null) || (typeField.getLastValue().getText() == "cvs")) return null
        return "module field is allowed only with cvs repository type"
    }
}
