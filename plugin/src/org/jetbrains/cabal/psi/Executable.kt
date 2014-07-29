package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

/**
 * @author Evgeny.Kurbatsky
 */
public class Executable(node: ASTNode) : Section(node) {

    public fun getExecutableName() : String {
        return getChildren()[0].getText()!!
    }

    public override fun getRequiredFieldNames(): List<String> = listOf("main-is")

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(EXECUTABLE_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }

}