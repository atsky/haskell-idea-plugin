package org.jetbrains.yesod.julius;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class JuliusFile extends PsiFileBase{
    public JuliusFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, JuliusLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return JuliusFileType.INSTANCE;
    }
}
