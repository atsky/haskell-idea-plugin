package org.jetbrains.cabal

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.cabal.psi.Executable
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.CachedValueProvider
import java.util.ArrayList
import org.json.simple.JSONArray
import org.json.simple.JSONObject

public class CabalFile(provider: FileViewProvider) : PsiFileBase(provider, CabalLanguage.INSTANCE) {
    public override fun getFileType(): FileType {
        return CabalFileType.INSTANCE
    }
    public override fun accept(visitor: PsiElementVisitor): Unit {
        visitor.visitFile(this)
    }

    public fun getExecutables(): MutableList<Executable> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<Executable>())
    }

}
