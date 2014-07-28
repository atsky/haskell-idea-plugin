package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

public class Library(node: ASTNode) : ASTWrapperPsiElement(node), Section {


    override public val REQUIRED_FIELD_NAMES = listOf ("exposed-modules")


    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(LIBRARY_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }
}
