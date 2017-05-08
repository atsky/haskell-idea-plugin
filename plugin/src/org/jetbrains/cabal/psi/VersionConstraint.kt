package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.highlight.ErrorMessage
import java.lang.IllegalStateException

class VersionConstraint(node: ASTNode) : PropertyValue(node), Checkable {

    companion object {
        val COMPARATORS: List<String> = listOf(
                ">=",
                "<=",
                ">",
                "<",
                "=="
        )
    }

    fun compareTo(other: String): Int? {
        if (!isSimple()) return null
        val thisVersion = getVersionValue().split('.').map({ it.toInt() })
        val otherVersion = other.split('.').map({ it.toInt() })

        fun compareFrom(i: Int): Int {
            if (i >= thisVersion.size) return -1
            if (i >= otherVersion.size) return 1
            if (thisVersion[i] == otherVersion[i]) return compareFrom(i + 1)
            return if (thisVersion[i] < otherVersion[i]) -1 else 1
        }
        return compareFrom(0)
    }

    fun satisfyConstraint(givenVersion: String): Boolean {
        val comparator = getComparator()
        val compareRes = compareTo(givenVersion)

        if (isAny()) return true

        if (!isSimple()) {
            val baseVersion = getVersionValue().substring(0, getVersionValue().length - 2)
            return givenVersion.startsWith(baseVersion)
        }

        if ((comparator == null) || (compareRes == null)) throw IllegalStateException()

        return when (comparator) {
            ">=" -> compareRes <= 0
            ">" -> compareRes < 0
            "<=" -> compareRes >= 0
            "<" -> compareRes > 0
            "==" -> compareRes == 0
            else -> throw IllegalStateException()
        }
    }

    fun isAny(): Boolean = text.equals("-any")

    fun isSimple(): Boolean = !(getVersionValue().endsWith('*'))

    fun getComparator(): String? = COMPARATORS.firstOrNull { it.equals(this.firstChild!!.text!!) }

    fun getVersion(): String = this.lastChild!!.text!!

    fun getVersionValue(): String = (getVersion().replace("(\\-[0-9a-zA-Z]+)+\\.".toRegex(), "\\.")).replace("(\\-[0-9a-zA-Z]+)+$".toRegex(), "")

    override fun check(): List<ErrorMessage> {
        val comparator = getComparator() ?: return listOf()
        val version = getVersion()
        if (parent!! is CabalVersionField) {
            if ((comparator == ">=") && (version.matches("[0-9]+\\.[0-9]+".toRegex()))) return listOf()
            return listOf(ErrorMessage(this, "invalid cabal version constraint", "error"))
        }
        if (comparator == "==") {
            if (version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.)*([0-9]+(\\-[0-9a-zA-Z]+)*)".toRegex()) || version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.){2,}\\*".toRegex())) return listOf()
        } else {
            if (version.matches("([0-9]+(\\-[0-9a-zA-Z]+)*\\.)*([0-9]+(\\-[0-9a-zA-Z]+)*)".toRegex())) return listOf()
        }
        return listOf(ErrorMessage(this, "invalid version constraint", "error"))
    }
}