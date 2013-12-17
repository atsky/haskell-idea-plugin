package org.jetbrains.haskell.run.cmd;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final class ConfigurationEditor extends SettingsEditor<CmdLineRunConfiguration> {

    private final ProgramParamsPanel programParams;

    ConfigurationEditor(Module[] modules) {
        programParams = new ProgramParamsPanel(modules);
    }

    protected void applyEditorTo(CmdLineRunConfiguration s) {
        programParams.applyTo(s);
    }

    protected void resetEditorFrom(CmdLineRunConfiguration s) {
        programParams.reset(s);
    }

    @NotNull
    protected JComponent createEditor() {
        return programParams;
    }

    protected void disposeEditor() {
    }
}
