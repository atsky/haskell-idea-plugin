package org.jetbrains.yesod.lucius;

/**
 * @author Leyla H
 */
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.icons.HaskellIcons;
import org.jetbrains.yesod.lucius.LuciusLanguage;

import javax.swing.*;

public class LuciusFileType extends LanguageFileType {
    public static final LuciusFileType INSTANCE = new LuciusFileType();
    @NonNls
    public static final String DEFAULT_EXTENSION = "lucius";

    private Icon myIcon;

    private LuciusFileType() {
        super(LuciusLanguage.INSTANCE);
        myIcon = HaskellIcons.HAMLET;
    }

    @NotNull
    public String getName() {
        return "Lucius file";
    }

    @NotNull
    public String getDescription() {
        return "Lucius file";
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