package org.jetbrains.cabal;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class CabalFile extends PsiFileBase {

    public CabalFile(@NotNull FileViewProvider provider) {
        super(provider, CabalLanguage.INSTANCE);
    }

    @NotNull
    public FileType getFileType() {
        return CabalFileType.INSTANCE;
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitFile(this);
    }
}