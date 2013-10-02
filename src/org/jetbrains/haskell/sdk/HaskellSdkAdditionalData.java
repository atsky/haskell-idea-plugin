package org.jetbrains.haskell.sdk;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public final class HaskellSdkAdditionalData implements SdkAdditionalData {
    private static final String GHC_OPTIONS = "GhcOptions";
    private static final String CABAL_PATH = "CabalPath";

    private String myGhcOptions = "";
    private String myCabalPath = "";

    public HaskellSdkAdditionalData() {

    }

    public HaskellSdkAdditionalData(Element element) {
        this.myGhcOptions = element.getAttributeValue(GHC_OPTIONS);
    }

    public void checkValid(SdkModel sdkModel) throws ConfigurationException {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void save(Element element) {
        if (myGhcOptions != null) {
            element.setAttribute(GHC_OPTIONS, myGhcOptions);
            element.setAttribute(CABAL_PATH, myCabalPath);
        }
    }

    public String getGhcOptions() {
        return myGhcOptions == null ? "" : myGhcOptions;
    }

    public void setGhcOptions(String ghcOptions) {
        this.myGhcOptions = ghcOptions;
    }

    @NotNull
    public String getCabalPath() {
        return myCabalPath == null ? "" : myCabalPath;
    }

    public void setCabalPath(String cabalPath) {
        this.myCabalPath = cabalPath;
    }
}
