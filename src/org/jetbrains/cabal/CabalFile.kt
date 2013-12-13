package org.jetbrains.cabal

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.cabal.psi.Executable

public open class CabalFile(provider: FileViewProvider) : PsiFileBase(provider, CabalLanguage.INSTANCE) {
    public override fun getFileType(): FileType {
        return CabalFileType.INSTANCE
    }
    public override fun accept(visitor: PsiElementVisitor): Unit {
        visitor.visitFile(this)
    }
    public open fun getExecutables(): MutableList<Executable> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<Executable>())
    }


}
