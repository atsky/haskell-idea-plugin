package org.jetbrains.haskell.sdk;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import org.jdom.Element;
import org.jetbrains.haskell.util.GHCUtil;

import java.io.File;

public final class HaskellSdkAdditionalData implements SdkAdditionalData {
    private static final String GHC_OPTIONS = "ghcOptions";

    private String ghcOptions = "";

    public HaskellSdkAdditionalData() {

    }

    public HaskellSdkAdditionalData(Element element) {
        this.ghcOptions = element.getAttributeValue(GHC_OPTIONS);
    }

    public void checkValid(SdkModel sdkModel) throws ConfigurationException {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void save(Element element) {
        if (ghcOptions != null) {
            element.setAttribute(GHC_OPTIONS, ghcOptions);
        }
    }

    public String getGhcOptions() {
        return ghcOptions == null ? "" : ghcOptions;
    }

    public void setGhcOptions(String ghcOptions) {
        this.ghcOptions = ghcOptions;
    }
}
