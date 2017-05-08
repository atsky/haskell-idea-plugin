package org.jetbrains.cabal.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.cabal.CabalFile
import java.io.File
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem

interface FieldContainer: PsiElement {

    fun getAvailableFieldNames(): List<String> = listOf()

    fun <T : Field> getField(fieldType: Class<T>): T? {
        return PsiTreeUtil.findChildOfType(this, fieldType)
    }

    fun <T : Field> getFields(fieldType: Class<T>): List<T> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, fieldType)
    }

    fun <T : Field> getField(fieldType: Class<T>, fieldName: String): T?
            = PsiTreeUtil.getChildrenOfTypeAsList(this, fieldType).firstOrNull { it.hasName(fieldName) }

    fun <T : Field> getFields(fieldType: Class<T>, fieldName: String): List<T>
            = PsiTreeUtil.getChildrenOfTypeAsList(this, fieldType).filter { it.hasName(fieldName) }

    fun getCabalFile(): CabalFile = (containingFile as CabalFile)

    fun getCabalVirtualFile(): VirtualFile? = getCabalFile().virtualFile

    fun getCabalRootPath(): String? = getCabalRootFile()?.path

    fun getCabalRootFile(): VirtualFile? = getCabalVirtualFile()?.parent

    fun getFileSystem(): VirtualFileSystem? = getCabalVirtualFile()?.fileSystem
}