package org.jetbrains.cabal.settings

import com.intellij.openapi.externalSystem.settings.ExternalSystemSettingsListener
import com.intellij.util.messages.Topic
import org.jetbrains.cabal.settings.CabalProjectSettings


interface CabalSettingsListener: ExternalSystemSettingsListener<CabalProjectSettings> {

    companion object {
        val TOPIC: Topic<CabalSettingsListener> = Topic.create("Cabal-specific settings", CabalSettingsListener::class.java)!!
    }
}