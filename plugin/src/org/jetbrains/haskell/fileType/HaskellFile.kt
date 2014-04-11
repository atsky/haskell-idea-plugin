package org.jetbrains.haskell.fileType

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.haskell.psi.ModuleName
import org.jetbrains.haskell.psi.Module

public class HaskellFile(provider: FileViewProvider) : PsiFileBase(provider, HaskellLanguage.INSTANCE) {

    override fun getFileType(): FileType {
        return HaskellFileType.INSTANCE
    }

    override fun accept(visitor: PsiElementVisitor) {
        visitor.visitFile(this)
    }

    public fun getModule() : Module? = findChildByClass(javaClass<Module>())
}