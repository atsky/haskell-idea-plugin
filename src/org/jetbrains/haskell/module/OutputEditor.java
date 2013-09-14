package org.jetbrains.haskell.module;

import com.intellij.openapi.roots.ui.configuration.BuildElementsEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

final class OutputEditor extends ModuleElementsEditor {

    private final BuildElementsEditor myCompilerOutputEditor;

    OutputEditor(ModuleConfigurationState state) {
        super(state);
        myCompilerOutputEditor = new BuildElementsEditor(state) {
        };
    }

    protected JComponent createComponentImpl() {
        return myCompilerOutputEditor.createComponentImpl();
    }

    public void saveData() {
        myCompilerOutputEditor.saveData();
    }

    @Nls
    public String getDisplayName() {
        return "Paths";
    }

    public String getHelpTopic() {
        return myCompilerOutputEditor.getHelpTopic();
    }
}
