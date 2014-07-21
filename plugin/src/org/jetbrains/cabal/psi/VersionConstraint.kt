package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Checkable
import com.intellij.psi.PsiElement

public class VersionConstraint(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun isValidValue(): String? {
        if ((this : PsiElement).getFirstChild()!!.getText()!!.equals("==")) {
            if ((this : PsiElement).getLastChild()!!.getText()!!.matches("([0-9]+\\.)*([0-9]+(\\.\\*)?)")) return null
        }
        else {
            if ((this : PsiElement).getLastChild()!!.getText()!!.matches("([0-9]+\\.)*([0-9]+)")) return null
        }
        return "invalid version constraint"
    }

}