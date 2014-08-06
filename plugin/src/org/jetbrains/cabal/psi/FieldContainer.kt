package org.jetbrains.cabal.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.cabal.CabalFile
import java.io.File
import com.intellij.openapi.vfs.VirtualFile

public trait FieldContainer: PsiElement {

    public fun <T : PsiElement> getField(fieldType: Class<T>): T? {
        return PsiTreeUtil.findChildOfType(this, fieldType)
    }

    public fun <T : PsiElement> getFields(fieldType: Class<T>): List<T>? {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, fieldType)
    }

    public fun <T : PsiElement> getField(fieldType: Class<T>, fieldName: String): T? {
        val fields = PsiTreeUtil.getChildrenOfTypeAsList(this, fieldType)
        for (field in fields) {
            if ((field is PropertyField) && (field.getPropertyName().equalsIgnoreCase(fieldName))) return field
            if ((field is Section)       && (field.getSectType().equalsIgnoreCase(fieldName)))     return field
        }
        return null
    }

    public fun getCabalFile(): CabalFile = (getContainingFile() as CabalFile)

    public fun getCabalVirtualFile(): VirtualFile? = getCabalFile().getVirtualFile()

    public fun getCabalRootPath(): String = getCabalVirtualFile()?.getParent()!!.getPath()

    //public fun getCabalRootDir(): File = File(getCabalRootPath())
}