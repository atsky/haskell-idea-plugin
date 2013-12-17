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

    public static String getExeName(String file) {
        return SystemInfo.isWindows
            ? file + ".exe"
            : file;
    }

    public static String getGhcCommandPath(VirtualFile ghcHome) {
        return getCommandPath(ghcHome, "ghc");
    }

    public static String getCommandPath(VirtualFile ghcHome, String executable) {
        if (ghcHome == null)
            return null;
        VirtualFile virBin = ghcHome.findChild("bin");
        if (virBin == null)
            return null;
        return new File(virBin.getPath(), executable).getAbsolutePath();
    }

    public static String rootsAsString(@NotNull Module module, boolean tests) {
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(tests);
        if (sourceRoots.length <= 0)
            return ".";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < sourceRoots.length; i++) {
            if (i > 0) {
                buf.append(':');
            }
            VirtualFile root = sourceRoots[i];
            buf.append(root.getPath());
        }
        return buf.toString();
    }

    @NotNull
    public static GHCVersion getVersion(@Nullable String name) {
        String[] versionStr = name == null ? new String[0] : name.split("[^0-9]");
        List<Integer> parts = new ArrayList<Integer>();
        for (String part : versionStr) {
            if (part.isEmpty())
                continue;
            try {
                parts.add(new Integer(part));
            } catch (NumberFormatException nfex) {
                // ignore
            }
        }
        return new GHCVersion(parts);
    }
}
