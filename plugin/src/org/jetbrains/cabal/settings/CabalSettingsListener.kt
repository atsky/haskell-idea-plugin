package org.jetbrains.cabal.settings

import com.intellij.openapi.externalSystem.settings.ExternalSystemSettingsListener
import com.intellij.util.messages.Topic
import org.jetbrains.cabal.settings.CabalProjectSettings


public trait CabalSettingsListener: ExternalSystemSettingsListener<CabalProjectSettings> {

    class object {
        public val TOPIC: Topic<CabalSettingsListener> = Topic.create("Cabal-specific settings", javaClass<CabalSettingsListener>())!!
    }
}