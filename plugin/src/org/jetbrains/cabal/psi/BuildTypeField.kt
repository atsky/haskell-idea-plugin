package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable
import com.intellij.psi.PsiElement

public class BuildTypeField(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    class object {
        public val VALID_BUILD_TYPES : List<String> = listOf(
                "Simple",
                "Configure",
                "Custom",
                "Make"
        )
    }

    public override fun isValidPSIElem(): Boolean {
        if ((this : PsiElement).getLastChild()!!.getText() in VALID_BUILD_TYPES) return true
        return false
    }
}
