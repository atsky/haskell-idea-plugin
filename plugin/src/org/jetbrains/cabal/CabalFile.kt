package org.jetbrains.cabal

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.CachedValueProvider
import java.util.ArrayList
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jetbrains.cabal.psi.*
import org.jetbrains.cabal.parser.*
import com.intellij.psi.PsiElement
import com.intellij.openapi.vfs.VirtualFile
import java.io.File


public class CabalFile(provider: FileViewProvider) : PsiFileBase(provider, CabalLanguage.INSTANCE), FieldContainer {

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(PKG_DESCR_FIELDS.keys)
        res.addAll(TOP_SECTION_NAMES)
        return res
    }

    public override fun getFileType(): FileType {
        return CabalFileType.INSTANCE
    }
    public override fun accept(visitor: PsiElementVisitor): Unit {
        visitor.visitFile(this)
    }

    public fun getExecutables(): MutableList<Executable> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, Executable::class.java)
    }

    public fun getFlagNames(): List<String> = PsiTreeUtil.getChildrenOfTypeAsList(this, Flag::class.java) map { it.getFlagName() }

    public fun getDataDir(): Path? = getField(DataDirField::class.java)?.getValue() as Path?
}
