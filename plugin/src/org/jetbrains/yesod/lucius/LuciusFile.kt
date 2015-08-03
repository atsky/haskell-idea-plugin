package org.jetbrains.yesod.lucius

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.jetbrains.yesod.lucius.LuciusFileType
import org.jetbrains.yesod.lucius.LuciusLanguage

public class LuciusFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, LuciusLanguage.INSTANCE) {

    override fun getFileType(): FileType {
        return LuciusFileType.INSTANCE
    }
}
