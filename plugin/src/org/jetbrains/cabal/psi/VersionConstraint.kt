package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.highlight.ErrorMessage
import java.lang.IllegalStateException

public class VersionConstraint(node: ASTNode) : ASTWrapperPsiElement(node), Checkable, PropertyValue {

    companion object {
        val COMPARATORS: List<String> = listOf(
                ">=",
                "<=",
                ">",
                "<",
                "=="
        )
    }

    public fun compareTo(other: String): Int? {
        if (!isSimple()) return null
        val thisVersion  = getVersionValue().split('.') map { it.toInt() }
        val otherVersion = other.split('.') map { it.toInt() }

        fun compareFrom(i: Int): Int {
            if (i >= thisVersion.size())  return -1
            if (i >= otherVersion.size()) return 1
            if (thisVersion[i] == otherVersion[i]) return compareFrom(i + 1)
            return if (thisVersion[i] < otherVersion[i]) -1 else 1
        }
        return compareFrom(0)
    }

    public fun satisfyConstraint(givenVersion: String): Boolean {
        val comparator = getComparator()
        val compareRes = compareTo(givenVersion)

        if (isAny()) return true

        if (!isSimple()) {
            val baseVersion = getVersionValue().get(0, getVersionValue().length() - 2) as String
            return givenVersion startsWith baseVersion
        }

        if ((comparator == null) || (compareRes == null)) throw IllegalStateException()

        return when (comparator) {
            ">=" -> compareRes <= 0
            ">"  -> compareRes <  0
            "<=" -> compareRes >= 0
            "<"  -> compareRes >  0
            "==" -> compareRes == 0
            else -> throw IllegalStateException()
        }
    }

    public fun isAny(): Boolean = getText().equals("-any")

    public fun isSimple(): Boolean = !(getVersionValue() endsWith '*')

    public fun getComparator(): String? = COMPARATORS firstOrNull { it.equals((this : PsiElement).getFirstChild()!!.getText()!!) }

    public fun getVersion(): String = (this : PsiElement).getLastChild()!!.getText()!!

    public fun getVersionValue(): String = (getVersion().replaceAll("(\\-[0-9a-zA-Z]+)+\\.", "\\.")).replaceAll("(\\-[0-9a-zA-Z]+)+$", "")

    public override fun check(): List<ErrorMessage> {
        val comparator = getComparator()
        if (comparator == null) return listOf()
        val version = getVersion()
        if (getParent()!! is CabalVersionField) {
            if ((comparator == ">=") && (version.matches("[0-9]+\\.[0-9]+"))) return listOf()
            return listOf(ErrorMessage(this, "invalid cabal version constraint", "error"))
        }
        if (comparator == "==") {
            if (version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.)*([0-9]+(\\-[0-9a-zA-Z]+)*)") || version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.){2,}\\*")) return listOf()
        }
        else {
            if (version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.)*([0-9]+(\\-[0-9a-zA-Z]+)*)")) return listOf()
        }
        return listOf(ErrorMessage(this, "invalid version constraint", "error"))
    }
}