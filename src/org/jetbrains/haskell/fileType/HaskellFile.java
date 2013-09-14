package org.jetbrains.haskell.fileType;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.fileType.HaskellFileType;
import org.jetbrains.haskell.HaskellLanguage;

public class HaskellFile extends PsiFileBase {

    public HaskellFile(@NotNull FileViewProvider provider) {
        super(provider, HaskellLanguage.INSTANCE);
    }

    @NotNull
    public FileType getFileType() {
        return HaskellFileType.INSTANCE;
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitFile(this);
    }
}