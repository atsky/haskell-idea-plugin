package org.jetbrains.haskell.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class GHCUtil {

    public static String getCommandPath(VirtualFile ghcHome, String executable) {
        if (ghcHome == null)
            return null;
        VirtualFile virBin = ghcHome.findChild("bin");
        if (virBin == null)
            return null;
        return new File(virBin.getPath(), executable).getAbsolutePath();
    }



}
