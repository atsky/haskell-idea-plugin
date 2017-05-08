package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

class Library(node: ASTNode) : BuildSection(node) {

    override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(LIBRARY_FIELDS.keys)
        res.addAll(IF_ELSE)
        return res
    }

    override fun getSectName(): String? = null
}
