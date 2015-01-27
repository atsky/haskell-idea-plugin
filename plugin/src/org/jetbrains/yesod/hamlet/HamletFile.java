package org.jetbrains.yesod.hamlet;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class HamletFile extends PsiFileBase{
    public HamletFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, HamletLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return HamletFileType.INSTANCE;
    }
}
