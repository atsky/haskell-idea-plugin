package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

/**
 * @author Evgeny.Kurbatsky
 */
public class Executable(node: ASTNode) : ASTWrapperPsiElement(node), Section {
    public fun getExecutableName() : String {
        return getChildren()[0].getText()!!
    }

    public override val REQUIRED_FIELD_NAMES = listOf ("main-is")

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(EXECUTABLE_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }

}