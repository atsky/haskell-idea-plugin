package org.jetbrains.cabal.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.cabal.CabalFile
import java.io.File
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import kotlin.Set

public trait FieldContainer: PsiElement {

    public open fun getAvailableFieldNames(): List<String> = listOf()

    public fun <T : Field> getField(fieldType: Class<T>): T? {
        return PsiTreeUtil.findChildOfType(this, fieldType)
    }

    public fun <T : Field> getFields(fieldType: Class<T>): List<T> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, fieldType)
    }

    public fun <T : Field> getField(fieldType: Class<T>, fieldName: String): T?
            = PsiTreeUtil.getChildrenOfTypeAsList(this, fieldType) firstOrNull { it.hasName(fieldName) }

    public fun <T : Field> getFields(fieldType: Class<T>, fieldName: String): List<T>
            = PsiTreeUtil.getChildrenOfTypeAsList(this, fieldType) filter { it.hasName(fieldName) }

    public fun getCabalFile(): CabalFile = (getContainingFile() as CabalFile)

    public fun getCabalVirtualFile(): VirtualFile? = getCabalFile().getVirtualFile()

    public fun getCabalRootPath(): String? = getCabalRootFile()?.getPath()

    public fun getCabalRootFile(): VirtualFile? = getCabalVirtualFile()?.getParent()

    public fun getFileSystem(): VirtualFileSystem? = getCabalVirtualFile()?.getFileSystem()
}