package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.Field
import org.jetbrains.cabal.parser.Disallowedable
import com.intellij.psi.PsiElement

public class TestModuleField(node: ASTNode) : ASTWrapperPsiElement(node), Field, Disallowedable {

    public override fun isEnabled(): String? {
        val parent = (this : PsiElement).getParent()!!
        if (parent is SourceRepo) {
            val sectType = parent.getFieldValue("type")
            if ((sectType == null) || (sectType == "exitcode-stdio-1.0")) return null
            return "test-module field disallowed with such test suit type"
        }
        return null
    }
}
