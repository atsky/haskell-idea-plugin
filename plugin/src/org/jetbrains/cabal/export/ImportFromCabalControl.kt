package org.jetbrains.cabal.export

import com.intellij.openapi.externalSystem.service.settings.AbstractImportFromExternalSystemControl
import com.intellij.openapi.externalSystem.util.ExternalSystemSettingsControl
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.cabal.settings.CabalProjectSettings
import org.jetbrains.cabal.settings.CabalSettings
import org.jetbrains.cabal.settings.CabalSettingsListener
import org.jetbrains.cabal.util.*

public class ImportFromCabalControl()
        : AbstractImportFromExternalSystemControl<CabalProjectSettings, CabalSettingsListener, CabalSettings>(
                SYSTEM_ID,
                CabalSettings(ProjectManager.getInstance()!!.getDefaultProject()),
                CabalProjectSettings(),
                true
        ) {

    override fun createProjectSettingsControl(settings: CabalProjectSettings): ExternalSystemSettingsControl<CabalProjectSettings> {
        val settingsControl = CabalProjectSettingsControl(settings)
        //settingsControl.hideUseAutoImportBox()
        return settingsControl
    }

    override fun createSystemSettingsControl(settings: CabalSettings): ExternalSystemSettingsControl<CabalSettings>? {
        return null
    }

    override fun onLinkedProjectPathChange(path: String) {
    }
}
