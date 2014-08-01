package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.Name
import org.jetbrains.cabal.psi.Section

public class Flag(node: ASTNode) : Section(node) {

    public override fun getRequiredFieldNames(): List<String> = listOf()

    public override fun getAvailableFieldNames(): List<String> {
        return FLAG_FIELDS
    }

    public fun getFlagName(): String {
        var node = getFirstChild()!!
        while (node !is Name) {
            node = node.getNextSibling()!!
        }
        return (node as Name).getText().toLowerCase()
    }

}
