package org.jetbrains.yesod.cassius;

/**
 * @author Leyla H
 */
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.icons.HaskellIcons;
import org.jetbrains.yesod.cassius.CassiusLanguage;

import javax.swing.*;

public class CassiusFileType extends LanguageFileType {
    public static final CassiusFileType INSTANCE = new CassiusFileType();
    @NonNls
    public static final String DEFAULT_EXTENSION = "cassius";

    private Icon myIcon;

    private CassiusFileType() {
        super(CassiusLanguage.INSTANCE);
        myIcon = HaskellIcons.HAMLET;
    }

    @NotNull
    public String getName() {
        return "Cassius file";
    }

    @NotNull
    public String getDescription() {
        return "Cassius file";
    }

    @NotNull
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    public Icon getIcon() {
        return myIcon;
    }

    public String getCharset(@NotNull VirtualFile file, final byte[] content) {
        return "UTF-8";
    }
}