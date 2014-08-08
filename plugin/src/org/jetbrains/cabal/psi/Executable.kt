package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.Name
import org.jetbrains.cabal.highlight.ErrorMessage
import java.util.ArrayList
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.lang.IllegalStateException
import com.intellij.psi.PsiElement

/**
 * @author Evgeny.Kurbatsky
 */
public class Executable(node: ASTNode) : BuildSection(node) {

    public fun getExecutableName(): String {
        val res = getSectName()
        if (res == null) throw IllegalStateException()
        return res
    }

    public override fun checkFieldsPresence(): List<ErrorMessage> {
        if (getField(javaClass<MainFileField>()) == null) return listOf(ErrorMessage(getSectTypeNode(), "main-is field is required", "error"))
        return listOf()
    }

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(EXECUTABLE_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }

    public fun getMainFile(): Path? = getField(javaClass<MainFileField>())?.getValue() as Path?
}