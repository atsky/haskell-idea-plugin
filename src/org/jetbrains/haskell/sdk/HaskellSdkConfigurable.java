package org.jetbrains.haskell.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.haskell.util.GHCUtil;

import javax.swing.*;
import java.io.File;
import java.util.StringTokenizer;

public final class HaskellSdkConfigurable implements AdditionalDataConfigurable {

    private static final Logger LOG = Logger.getInstance("ideah.sdk.HaskellSdkConfigurable");

    private final HaskellSdkConfigurableForm myForm;

    private Sdk mySdk;

    public HaskellSdkConfigurable() {
        myForm = new HaskellSdkConfigurableForm();
    }

    @Nullable
    private static String suggestLibPath(@Nullable Sdk sdk) {
        if (sdk == null)
            return null;
        VirtualFile ghcHome = sdk.getHomeDirectory();
        if (ghcHome == null)
            return null;
        String ghcLib = null;
        try {
            String ghcCommandPath = GHCUtil.getGhcCommandPath(ghcHome);
            if (ghcCommandPath == null)
                return null;
        } catch (Exception e) {
            LOG.error(e);
        }
        return ghcLib;
    }

    @Nullable
    private static String getPathFor(String exeName) {
        String path = System.getenv("PATH");
        StringTokenizer stringTokenizer = new StringTokenizer(path, File.pathSeparator);
        while (stringTokenizer.hasMoreTokens()) {
            String dir = stringTokenizer.nextToken();
            File directory = new File(dir);
            if (directory.isDirectory()) {
                File file = new File(directory, exeName);
                if (file.isFile())
                    return file.getAbsolutePath();
            }
        }
        return null;
    }

    public void setSdk(Sdk sdk) {
        mySdk = sdk;
    }

    public JComponent createComponent() {
        return myForm.getContentPanel();
    }

    public boolean isModified() {
        return myForm.isModified();
    }

    public void apply() {
        HaskellSdkAdditionalData newData = new HaskellSdkAdditionalData();
        final SdkModificator modificator = mySdk.getSdkModificator();
        modificator.setSdkAdditionalData(newData);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                modificator.commitChanges();
            }
        });
        myForm.setModified(false);
    }

    public void reset() {
        SdkAdditionalData data = mySdk.getSdkAdditionalData();
        HaskellSdkAdditionalData ghcData;
        if (data != null) {
            if (!(data instanceof HaskellSdkAdditionalData))
                return;
            ghcData = (HaskellSdkAdditionalData) data;
        } else {
            ghcData = null;
        }
        boolean modified = false;
        String initialGhcOptions = "-W";
        String ghcOptions = ghcData == null ? initialGhcOptions : ghcData.getGhcOptions();
        myForm.init(ghcOptions);
        myForm.setModified(modified);
    }

    public void disposeUIResources() {
    }
}
