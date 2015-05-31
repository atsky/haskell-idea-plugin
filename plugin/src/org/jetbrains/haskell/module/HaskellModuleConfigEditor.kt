package org.jetbrains.haskell.module

import com.intellij.openapi.module.JavaModuleType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleConfigurationEditor
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor
import com.intellij.openapi.roots.ui.configuration.ContentEntriesEditor
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState
import java.util.ArrayList

public class HaskellModuleConfigEditor() : ModuleConfigurationEditorProvider {

    override fun createEditors(state: ModuleConfigurationState?): Array<ModuleConfigurationEditor> {
        val module = state!!.getRootModel()!!.getModule()

        return arrayOf(ContentEntriesEditor(module.getName(), state),
                //PackagesEditor(state, module.getProject()),
                OutputEditor(state));
    }
}
