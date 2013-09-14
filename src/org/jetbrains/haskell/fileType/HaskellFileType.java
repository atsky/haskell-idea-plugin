package org.jetbrains.haskell.fileType;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.HaskellLanguage;
import org.jetbrains.haskell.icons.HaskellIcons;

import javax.swing.*;

public class HaskellFileType extends LanguageFileType {
    public static final HaskellFileType INSTANCE = new HaskellFileType();
    @NonNls
    public static final String DEFAULT_EXTENSION = "hs";

    private Icon myIcon;

    private HaskellFileType() {
        super(HaskellLanguage.INSTANCE);
        myIcon = HaskellIcons.DEFAULT;
    }

    @NotNull
    public String getName() {
        return "Haskell file";
    }

    @NotNull
    public String getDescription() {
        return "Haskell file";
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