package org.jetbrains.cabal.settings

import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings
import com.intellij.openapi.externalSystem.settings.DelegatingExternalSystemSettingsListener
import com.intellij.openapi.externalSystem.settings.ExternalSystemSettingsListener
import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic
import org.jetbrains.cabal.settings.*


class CabalSettings(project: Project)
        : AbstractExternalSystemSettings<CabalSettings, CabalProjectSettings, CabalSettingsListener>(CabalSettingsListener.TOPIC, project) {

    override fun subscribe(listener: ExternalSystemSettingsListener<CabalProjectSettings>) {
        getProject().getMessageBus().connect(getProject()).subscribe(CabalSettingsListener.TOPIC as Topic<ExternalSystemSettingsListener<CabalProjectSettings>>, listener)
    }

    override fun copyExtraSettingsFrom(settings: CabalSettings) {
    }

    override fun checkSettings(old: CabalProjectSettings, current: CabalProjectSettings) {
//        if (!Comparing.equal(old.getCabalHome(), current.getCabalHome())) {
//            ....
//        }
    }
}
