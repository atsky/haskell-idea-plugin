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
public class Name(node: ASTNode) : ASTWrapperPsiElement(node), PropertyValue, RangedValue {

    public override fun getAvailableValues(): List<String> {
        val parent = getParent()
        if (isFlagNameInCondition()) {
            return (getContainingFile() as CabalFile).getFlagNames()
        }
        else if (parent is InvalidField) {
            return (parent.getParent() as FieldContainer).getAvailableFieldNames()

        }
        return listOf()
    }

    public override fun check(): List<ErrorMessage> {
        if (isFlagNameInCondition()) {
            if (getText().toLowerCase() in (getContainingFile() as CabalFile).getFlagNames()) return listOf()
            return listOf(ErrorMessage(this, "invalid flag name", "error"))
        }
        if (getParent() is Section) {
            if (getNode().getText()!!.matches("^\\S+$")) return listOf()
            return listOf(ErrorMessage(this, "invalid section name", "error"))
        }
        if (getNode().getText()!!.matches("^([a-zA-Z0-9]+-)*[a-zA-Z0-9]+$")) return listOf()
        return listOf(ErrorMessage(this, "invalid name", "error"))
    }

    public fun isFlagNameInCondition(): Boolean {
        val parent = getParent()!!
        if ((parent is SimpleCondition) && (parent.getTestName() == "flag")) return true
        if (parent is InvalidConditionPart) {
            var prevElement = getPrevSibling()
            while (prevElement != null) {
                if (prevElement?.getText() == "flag") return true
                prevElement = prevElement?.getPrevSibling()
            }
        }
        return false
    }
}