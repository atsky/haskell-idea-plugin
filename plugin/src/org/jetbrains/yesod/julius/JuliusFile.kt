package org.jetbrains.yesod.julius

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class JuliusFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, JuliusLanguage.INSTANCE) {

    override fun getFileType(): FileType {
        return JuliusFileType.INSTANCE
    }
}
