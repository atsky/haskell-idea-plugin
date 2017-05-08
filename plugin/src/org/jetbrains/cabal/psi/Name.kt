package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.psi.RangedValue
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.cabal.CabalFile
import org.jetbrains.cabal.highlight.ErrorMessage

/**
 * Created by atsky on 13/12/13.
 */
class Name(node: ASTNode) : PropertyValue(node), RangedValue {

    override fun getAvailableValues(): List<String> {
        val parent = parent
        if (isFlagNameInCondition()) {
            return (containingFile as CabalFile).getFlagNames()
        }
        else if (parent is InvalidField) {
            return (parent.getParent() as FieldContainer).getAvailableFieldNames()

        }
        return listOf()
    }

    override fun check(): List<ErrorMessage> {
        if (isFlagNameInCondition()) {
            if (text.toLowerCase() in (containingFile as CabalFile).getFlagNames()) return listOf()
            return listOf(ErrorMessage(this, "invalid flag name", "error"))
        }
        if (parent is Section) {
            if (node.text.matches("^\\S+$".toRegex())) return listOf()
            return listOf(ErrorMessage(this, "invalid section name", "error"))
        }
        if (node.text.matches("^([a-zA-Z0-9]+-)*[a-zA-Z0-9]+$".toRegex())) return listOf()
        return listOf(ErrorMessage(this, "invalid name", "error"))
    }

    fun isFlagNameInCondition(): Boolean {
        val parent = parent!!
        if ((parent is SimpleCondition) && (parent.getTestName() == "flag")) return true
        if (parent is InvalidConditionPart) {
            var prevElement = prevSibling
            while (prevElement != null) {
                if (prevElement.text == "flag") return true
                prevElement = prevElement.prevSibling
            }
        }
        return false
    }
}