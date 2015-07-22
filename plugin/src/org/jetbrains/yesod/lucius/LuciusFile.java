package org.jetbrains.yesod.lucius;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yesod.lucius.LuciusFileType;
import org.jetbrains.yesod.lucius.LuciusLanguage;

public class LuciusFile extends PsiFileBase{
    public LuciusFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, LuciusLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return LuciusFileType.INSTANCE;
    }
}
