package org.jetbrains.haskell.run.haskell;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final class ConfigurationEditor extends SettingsEditor<CabalRunConfiguration> {

    private final ProgramParamsPanel programParams;

    ConfigurationEditor(Module[] modules) {
        programParams = new ProgramParamsPanel(modules);
    }

    protected void applyEditorTo(CabalRunConfiguration s) {
        programParams.applyTo(s);
    }

    protected void resetEditorFrom(CabalRunConfiguration s) {
        programParams.reset(s);
    }

    @NotNull
    protected JComponent createEditor() {
        return programParams;
    }

    protected void disposeEditor() {
    }
}
