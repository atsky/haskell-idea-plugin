package org.jetbrains.generator.grammar

/**
 * Created by atsky on 11/7/14.
 */
class RuleRef(val text : String, val isName : Boolean) {
    override fun toString(): String {
        return if (isName) text else "'" + text + "'";
    }

    override fun hashCode(): Int {
        return text.hashCode() + isName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is RuleRef) {
            return false
        }
        return text == other.text && isName == other.isName
    }
}