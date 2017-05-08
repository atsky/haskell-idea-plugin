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

class HaskellModuleConfigEditor : ModuleConfigurationEditorProvider {

    override fun createEditors(state: ModuleConfigurationState?): Array<ModuleConfigurationEditor> {
        val module = state!!.rootModel!!.module

        return arrayOf(ContentEntriesEditor(module.name, state),
                //PackagesEditor(state, module.getProject()),
                OutputEditor(state))
    }
}
