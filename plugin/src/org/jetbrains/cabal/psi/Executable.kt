package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.lang.IllegalStateException

/**
 * @author Evgeny.Kurbatsky
 */
public class Executable(node: ASTNode) : BuildSection(node) {

    public fun getExecutableName(): String {
        return getAfterTypeNode()!!.getText()!!
    }

    public override fun getRequiredFieldNames(): List<String> = listOf("main-is")

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(EXECUTABLE_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }

    public fun getMainFile(): Path? = getField(javaClass<MainFileField>())?.getValue() as Path?
}