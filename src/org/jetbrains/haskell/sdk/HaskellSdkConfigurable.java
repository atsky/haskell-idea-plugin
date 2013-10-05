package org.jetbrains.haskell.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModificator;

import javax.swing.*;

public final class HaskellSdkConfigurable implements AdditionalDataConfigurable {
    private final HaskellSdkConfigurableForm myForm;

    private Sdk mySdk;

    public HaskellSdkConfigurable() {
        myForm = new HaskellSdkConfigurableForm();
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
        HaskellSdkAdditionalData newData = new HaskellSdkAdditionalData(myForm.getGhcOptions(), myForm.getCabalPath());

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
        String cabalPath  = ghcData == null ? "" : ghcData.getCabalPath();
        myForm.init(ghcOptions, cabalPath);
        myForm.setModified(modified);
    }

    public void disposeUIResources() {
    }
}
