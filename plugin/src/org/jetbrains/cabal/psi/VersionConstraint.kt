package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.PropertyValue

public class VersionConstraint(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public fun getComparator(): String = (this : PsiElement).getFirstChild()!!.getText()!!

    public fun getVersion(): String = (this : PsiElement).getLastChild()!!.getText()!!

    public override fun isValidValue(): String? {
        val version = getVersion()
        if (getParent()!! is CabalVersionField) {
            if ((getComparator() == ">=") && (version.matches("[0-9]+\\.[0-9]+"))) return null
            return "invalid cabal version constraint"
        }
        if (getComparator().equals("==")) {
            if (version.matches("([0-9]+(\\-[0-9a-zA_Z]+)*\\.)*([0-9]+(\\-[0-9a-zA_Z]+)*)") || version.matches("([0-9]+(\\-[0-9a-zA_Z]+)*\\.){2,}\\*")) return null
        }
        else {
            if (version.matches("([0-9]+(\\-[0-9a-zA_Z]+)*\\.)*([0-9]+(\\-[0-9a-zA_Z]+)*)")) return null
        }
        return "invalid version constraint"
    }

}