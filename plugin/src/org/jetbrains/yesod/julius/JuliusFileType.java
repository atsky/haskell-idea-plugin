package org.jetbrains.yesod.julius;

/**
 * @author Leyla H
 */
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.icons.HaskellIcons;

import javax.swing.*;

public class JuliusFileType extends LanguageFileType {
    public static final JuliusFileType INSTANCE = new JuliusFileType();
    @NonNls
    public static final String DEFAULT_EXTENSION = "julius";

    private Icon myIcon;

    private JuliusFileType() {
        super(JuliusLanguage.INSTANCE);
        myIcon = HaskellIcons.HAMLET;
    }

    @NotNull
    public String getName() {
        return "Julius file";
    }

    @NotNull
    public String getDescription() {
        return "Julius file";
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