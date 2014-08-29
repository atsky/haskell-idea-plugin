package org.jetbrains.cabal.settings

import com.intellij.openapi.externalSystem.settings.ExternalSystemSettingsListenerAdapter
import com.intellij.util.messages.Topic
import org.jetbrains.cabal.settings.CabalProjectSettings
import org.jetbrains.cabal.settings.CabalSettingsListener


public class CabalSettingsListenerImpl(): ExternalSystemSettingsListenerAdapter<CabalProjectSettings>(), CabalSettingsListener {

}