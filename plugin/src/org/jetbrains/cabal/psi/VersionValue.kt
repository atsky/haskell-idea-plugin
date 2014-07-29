package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.PsiElement

public class VersionValue(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun isValidValue(): String? {
        if (!(this : PsiElement).getText()!!.matches("([0-9]+(\\-[0-9a-zA_Z]+)*\\.)*([0-9]+(\\-[0-9a-zA_Z]+)*)")) return "invalid version"
        return null
    }
}
