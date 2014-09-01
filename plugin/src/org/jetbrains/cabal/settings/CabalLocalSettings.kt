package org.jetbrains.cabal.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.externalSystem.service.project.PlatformFacade
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemLocalSettings
import com.intellij.openapi.project.Project
import org.jetbrains.cabal.util.*

public class CabalLocalSettings(project: Project, facade: PlatformFacade)
        : AbstractExternalSystemLocalSettings(SYSTEM_ID, project, facade) {

    class object {
        fun getInstance(project: Project): CabalLocalSettings? {
            return ServiceManager.getService(project, javaClass<CabalLocalSettings>())
        }
    }
}