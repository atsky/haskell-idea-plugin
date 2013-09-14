package org.jetbrains.haskell.module;

import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;

import java.util.ArrayList;
import java.util.List;

public final class HaskellModuleConfigEditor implements ModuleConfigurationEditorProvider {

    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        List<ModuleConfigurationEditor> editors = new ArrayList<ModuleConfigurationEditor>();
        editors.add(new OutputEditor(state));
        return editors.toArray(new ModuleConfigurationEditor[editors.size()]);
    }
}
