package org.jetbrains.yesod.hamlet

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

public class HamletFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, HamletLanguage.INSTANCE) {

    override fun getFileType(): FileType {
        return HamletFileType.INSTANCE
    }
}
