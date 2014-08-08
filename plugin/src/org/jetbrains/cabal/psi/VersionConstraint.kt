package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.highlight.ErrorMessage

public class VersionConstraint(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    public fun satisfyConstraint(givenVersion: String): Boolean {
        val version = getVersionValue()
        if (getComparator().equals(">=")) return givenVersion >= version
        if (getComparator().equals("<=")) return givenVersion <= version
        if (isSimple()) return givenVersion == version
        val baseVersion = version.get(0, version.size - 2)!! as String
        return givenVersion startsWith baseVersion
    }

    public fun isSimple(): Boolean = !(getVersionValue() endsWith '*')

    public fun getComparator(): String = (this : PsiElement).getFirstChild()!!.getText()!!

    public fun getVersion(): String = (this : PsiElement).getLastChild()!!.getText()!!

    public fun getVersionValue(): String = (getVersion().replaceAll("(\\-[0-9a-zA-Z]+)+\\.", "\\.")).replaceAll("(\\-[0-9a-zA-Z]+)+$", "")

    public override fun checkValue(): List<ErrorMessage> {
        val version = getVersion()
        if (getParent()!! is CabalVersionField) {
            if ((getComparator() == ">=") && (version.matches("[0-9]+\\.[0-9]+"))) return listOf()
            return listOf(ErrorMessage(this, "invalid cabal version constraint", "error"))
        }
        if (getComparator().equals("==")) {
            if (version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.)*([0-9]+(\\-[0-9a-zA-Z]+)*)") || version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.){2,}\\*")) return listOf()
        }
        else {
            if (version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.)*([0-9]+(\\-[0-9a-zA-Z]+)*)")) return listOf()
        }
        return listOf(ErrorMessage(this, "invalid version constraint", "error"))
    }
}