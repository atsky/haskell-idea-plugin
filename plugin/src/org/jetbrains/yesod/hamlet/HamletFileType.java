package org.jetbrains.yesod.hamlet;

/**
 * @author Leyla H
 */
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.icons.HaskellIcons;

import javax.swing.*;

public class HamletFileType extends LanguageFileType {
    public static final HamletFileType INSTANCE = new HamletFileType();
    @NonNls
    public static final String DEFAULT_EXTENSION = "hamlet";

    private Icon myIcon;

    private HamletFileType() {
        super(HamletLanguage.INSTANCE);
        myIcon = HaskellIcons.HAMLET;
    }

    @NotNull
    public String getName() {
        return "Hamlet file";
    }

    @NotNull
    public String getDescription() {
        return "Hamlet file";
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