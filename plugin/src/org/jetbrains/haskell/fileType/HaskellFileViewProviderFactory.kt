package org.jetbrains.haskell.fileType

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.FileViewProviderFactory
import com.intellij.psi.PsiManager
import org.jetbrains.haskell.HaskellViewProvider

public class HaskellFileViewProviderFactory() : FileViewProviderFactory {

    override fun createFileViewProvider(file: VirtualFile,
                                        language: Language,
                                        manager: PsiManager,
                                        eventSystemEnabled: Boolean): FileViewProvider {
        return HaskellViewProvider(manager, file, eventSystemEnabled, language)
    }
}
