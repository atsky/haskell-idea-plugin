package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.Name
import org.jetbrains.cabal.psi.Section
import java.util.ArrayList

class Flag(node: ASTNode) : Section(node) {

    override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(FLAG_FIELDS.keys)
        return res
    }

    fun getFlagName(): String {
        val res = getSectName()?.toLowerCase()
        if (res == null) throw IllegalStateException()
        return res
    }
}
