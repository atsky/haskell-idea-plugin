package org.jetbrains.haskell.sdk;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.HashMap;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.haskell.util.ProcessRunner;

import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;

public final class HaskellSdkType extends SdkType {

    public static final HaskellSdkType INSTANCE = new HaskellSdkType();
    private static final Icon GHC_ICON = IconLoader.findIcon("/org/jetbrains/haskell/haskell_16.png");

    public HaskellSdkType() {
        super("GHC");
    }

    public String suggestHomePath() {
        File versionsRoot;
        String[] versions;
        String append;
        if (SystemInfo.isLinux) {
            versionsRoot = new File("/usr/lib");
            if (!versionsRoot.isDirectory())
                return null;
            versions = versionsRoot.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().startsWith("ghc") && new File(dir, name).isDirectory();
                }
            });
            append = null;
        } else if (SystemInfo.isWindows) {
            String progFiles = System.getenv("ProgramFiles(x86)");
            if (progFiles == null) {
                progFiles = System.getenv("ProgramFiles");
            }
            if (progFiles == null)
                return null;
            versionsRoot = new File(progFiles, "Haskell Platform");
            if (!versionsRoot.isDirectory())
                return progFiles;
            versions = versionsRoot.list();
            append = null;
        } else if (SystemInfo.isMac) {
            versionsRoot = new File("/Library/Frameworks/GHC.framework/Versions");
            if (!versionsRoot.isDirectory())
                return null;
            versions = versionsRoot.list();
            append = "usr";
        } else {
            return null;
        }
        String latestVersion = getLatestVersion(versions);
        if (latestVersion == null)
            return null;
        File versionDir = new File(versionsRoot, latestVersion);
        File homeDir;
        if (append != null) {
            homeDir = new File(versionDir, append);
        } else {
            homeDir = versionDir;
        }
        return homeDir.getAbsolutePath();
    }

    private static String getLatestVersion(String[] names) {
        if (names == null)
            return null;
        int length = names.length;
        if (length == 0)
            return null;
        if (length == 1)
            return names[0];
        List<GHCDir> ghcDirs = new ArrayList<GHCDir>();
        for (String name : names) {
            ghcDirs.add(new GHCDir(name));
        }
        Collections.sort(ghcDirs, new Comparator<GHCDir>() {
            public int compare(GHCDir d1, GHCDir d2) {
                return d1.version.compareTo(d2.version);
            }
        });
        return ghcDirs.get(ghcDirs.size() - 1).name;
    }

    public static boolean checkForGhc(File path) {
        File bin = new File(path, "bin");
        if (!bin.isDirectory())
            return false;
        File[] children = bin.listFiles(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return false;
                return "ghc".equalsIgnoreCase(FileUtil.getNameWithoutExtension(f));
            }
        });
        return children != null && children.length >= 1;
    }

    public boolean isValidSdkHome(String path) {
        return checkForGhc(new File(path));
    }

    public String suggestSdkName(String currentSdkName, String sdkHome) {
        String suggestedName;
        if (currentSdkName != null && currentSdkName.length() > 0) {
            suggestedName = currentSdkName;
        } else {
            String versionString = getVersionString(sdkHome);
            if (versionString != null) {
                suggestedName = "GHC " + versionString;
            } else {
                suggestedName = "Unknown";
            }
        }
        return suggestedName;
    }

    public String getVersionString(String sdkHome) {
        String versionString = getGhcVersion(sdkHome);
        if (versionString != null && versionString.length() == 0) {
            versionString = null;
        }

        return versionString;
    }

    @Nullable
    public static String getGhcVersion(String homePath) {
        if (homePath == null || !new File(homePath).isDirectory()) {
            return null;
        }
        try {
            String output = new ProcessRunner().execute(homePath + File.separator + "bin" + File.separator + "ghc",
                    "--numeric-version");
            return output.trim();
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }

    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return new HaskellSdkConfigurable();
    }

    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
        if (additionalData instanceof HaskellSdkAdditionalData) {
            ((HaskellSdkAdditionalData) additionalData).save(additional);
        }
    }

    @Override
    public SdkAdditionalData loadAdditionalData(Element additional) {
        return new HaskellSdkAdditionalData(additional);
    }

    public String getPresentableName() {
        return "GHC";
    }

    @Override
    public Icon getIcon() {
        return GHC_ICON;
    }

    @Override
    public Icon getIconForAddAction() {
        return getIcon();
    }

    @Override
    public String adjustSelectedSdkHome(String homePath) {
        return super.adjustSelectedSdkHome(homePath); // todo: if 'bin' or 'ghc' selected, choose parent folder
    }

    @Override
    public void setupSdkPaths(Sdk sdk) {

    }

    @Override
    public boolean isRootTypeApplicable(OrderRootType type) {
        return false;
    }

}
