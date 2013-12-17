package org.jetbrains.haskell.fileType;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class HiFileType implements FileType {

    public static final HiFileType INSTANCE = new HiFileType();


    @NotNull
    public String getName() {
        return "Haskell interface";
    }

    @NotNull
    public String getDescription() {
        return "Haskell interface file";
    }

    @NotNull
    public String getDefaultExtension() {
        return "hi";
    }

    public Icon getIcon() {
        return null;
    }

    public boolean isBinary() {
        return true;
    }

    public boolean isReadOnly() {
        return true;
    }

    public String getCharset(@NotNull VirtualFile file, byte[] content) {
        return null;
    }

}