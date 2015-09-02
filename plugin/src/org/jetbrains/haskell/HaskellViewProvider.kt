package org.jetbrains.haskell

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.SingleRootFileViewProvider

public class HaskellViewProvider(manager: PsiManager,
                                 virtualFile: VirtualFile,
                                 eventSystemEnabled: Boolean,
                                 language: Language) :
        SingleRootFileViewProvider(manager, virtualFile, eventSystemEnabled, language) {

    override fun supportsIncrementalReparse(rootLanguage: Language): Boolean {
        return false
    }

}
