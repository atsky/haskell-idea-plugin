package org.jetbrains.yesod.cassius;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yesod.cassius.CassiusFileType;
import org.jetbrains.yesod.cassius.CassiusLanguage;

public class CassiusFile extends PsiFileBase{
    public CassiusFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CassiusLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CassiusFileType.INSTANCE;
    }
}
