package org.jetbrains.cabal.export

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalProjectSettingsControl
import com.intellij.openapi.externalSystem.util.PaintAwarePanel
import com.intellij.openapi.options.ConfigurationException
import org.jetbrains.cabal.settings.CabalProjectSettings


public class CabalProjectSettingsControl(initialSettings: CabalProjectSettings)
                       : AbstractExternalProjectSettingsControl<CabalProjectSettings>(initialSettings) {

    override fun fillExtraControls(content: PaintAwarePanel, indentLevel: Int) {  }

    throws(javaClass<ConfigurationException>())
    override fun validate(settings: CabalProjectSettings): Boolean {
        return true
    }

    override fun applyExtraSettings(settings: CabalProjectSettings) {  }

    override fun isExtraSettingModified(): Boolean = false

    override fun resetExtraSettings(isDefaultModuleCreation: Boolean) { }
}

