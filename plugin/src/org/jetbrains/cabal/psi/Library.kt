package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

public class Library(node: ASTNode) : Section(node) {

    public override fun getRequiredFieldNames(): List<String> = listOf("exposed-modules")

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(LIBRARY_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }
}
