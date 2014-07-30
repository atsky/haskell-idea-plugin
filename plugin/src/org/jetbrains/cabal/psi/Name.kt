package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.psi.RangedValue
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.cabal.CabalFile

/**
 * Created by atsky on 13/12/13.
 */
public class Name(node: ASTNode) : ASTWrapperPsiElement(node), PropertyValue, RangedValue {

    public override fun getAvailableValues(): List<String> {
        if (isFlagName()) {
            return (getContainingFile() as CabalFile).getFlagNames()
        }
        return listOf()
    }

    public override fun isValidValue(): String? {
        if (isFlagName()) {
            if (getText() !in getAvailableValues()) {
                return "invalid flag name"
            }
            return null
        }
        if (!getNode().getText()!!.matches("^([a-zA-Z0-9]+-)*[a-zA-Z0-9]+$")) {
            return "invalid name"
        }
        return null
    }

    private fun isFlagName(): Boolean {
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