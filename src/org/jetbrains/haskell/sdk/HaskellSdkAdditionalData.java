package org.jetbrains.haskell.sdk;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import org.jdom.Element;
import org.jetbrains.haskell.util.GHCUtil;

import java.io.File;

public final class HaskellSdkAdditionalData implements SdkAdditionalData {

    private static final String LIB_PATH = "ghcLibPath";
    private static final String CABAL_PATH = "cabalPath";
    private static final String GHC_OPTIONS = "ghcOptions";

    private String libPath;
    private String cabalPath;
    private String ghcOptions;

    public HaskellSdkAdditionalData(String libPath, String cabalPath, String ghcOptions) {
        this.libPath = libPath;
        this.cabalPath = cabalPath;
        this.ghcOptions = ghcOptions;
    }

    public HaskellSdkAdditionalData(Element element) {
        this.libPath = element.getAttributeValue(LIB_PATH);
        this.cabalPath = element.getAttributeValue(CABAL_PATH);
        this.ghcOptions = element.getAttributeValue(GHC_OPTIONS);
    }

    public void checkValid(SdkModel sdkModel) throws ConfigurationException {
        // todo: changed in GHC 7?
        if (libPath == null || !new File(libPath, "package.conf.d").exists()) {
            throw new ConfigurationException("Invalid GHC lib directory (should contain 'package.conf.d')");
        }
        String cabal = GHCUtil.getExeName("cabal");
        if (cabalPath == null || !new File(cabalPath).getName().equals(cabal) || !new File(cabalPath).isFile()) {
            throw new ConfigurationException("Please indicate the full " + cabal + " file path");
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void save(Element element) {
        if (libPath != null) {
            element.setAttribute(LIB_PATH, libPath);
        }
        if (cabalPath != null) {
            element.setAttribute(CABAL_PATH, cabalPath);
        }
        if (ghcOptions != null) {
            element.setAttribute(GHC_OPTIONS, ghcOptions);
        }
    }

    public String getLibPath() {
        return libPath;
    }

    public String getCabalPath() {
        return cabalPath;
    }

    public String getGhcOptions() {
        return ghcOptions;
    }

    public void setLibPath(String libPath) {
        this.libPath = libPath;
    }

    public void setCabalPath(String cabalPath) {
        this.cabalPath = cabalPath;
    }

    public void setGhcOptions(String ghcOptions) {
        this.ghcOptions = ghcOptions;
    }
}
