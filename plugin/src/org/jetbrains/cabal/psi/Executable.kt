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
class Executable(node: ASTNode) : BuildSection(node) {

    fun getExecutableName(): String {
        val res = getSectName()
        if (res == null) throw IllegalStateException()
        return res
    }

    override fun check(): List<ErrorMessage> {
        if (getField(MainFileField::class.java) == null) return listOf(ErrorMessage(getSectTypeNode(), "main-is field is required", "error"))
        return listOf()
    }

    override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(EXECUTABLE_FIELDS.keys)
        res.addAll(IF_ELSE)
        return res
    }

    fun getMainFile(): Path? = getField(MainFileField::class.java)?.getValue() as Path?
}