package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

public class Library(node: ASTNode) : BuildSection(node) {

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(LIBRARY_FIELDS.keySet())
        res.addAll(BUILD_INFO_FIELDS.keySet())
        res.addAll(listOf("is", "else"))
        return res
    }

    protected override fun getSectName(): String? = null
}
