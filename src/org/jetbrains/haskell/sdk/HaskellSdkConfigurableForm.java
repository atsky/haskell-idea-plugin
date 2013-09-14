package org.jetbrains.haskell.sdk;

import javax.swing.*;

/**
 * @author Evgeny.Kurbatsky
 */
public class HaskellSdkConfigurableForm {
    private boolean modified;
    private String libPath;
    private String cabalPath;
    private String ghcOptions;

    public JComponent getContentPanel() {
        return new JPanel();
    }

    public boolean isModified() {
        return modified;
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

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void init(String ghcOptions) {

    }
}
