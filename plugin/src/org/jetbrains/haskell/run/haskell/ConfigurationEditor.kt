package org.jetbrains.haskell.run.haskell

import com.intellij.openapi.module.Module
import com.intellij.openapi.options.SettingsEditor
import javax.swing.*

class ConfigurationEditor(modules: Array<Module>) : SettingsEditor<CabalRunConfiguration>() {

    private val programParams: ProgramParamsPanel

    override fun applyEditorTo(s: CabalRunConfiguration?) {
        programParams.applyTo(s!!)
    }

    override fun resetEditorFrom(s: CabalRunConfiguration?) {
        programParams.reset(s!!)
    }

    override fun createEditor(): JComponent {
        return programParams
    }

    override fun disposeEditor() {
    }

    {
        programParams = ProgramParamsPanel(modules)
    }
}
