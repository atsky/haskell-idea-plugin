package org.jetbrains.cabal.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.externalSystem.service.project.PlatformFacadeImpl
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemLocalSettings
import com.intellij.openapi.project.Project
import org.jetbrains.cabal.util.*

public class CabalLocalSettings(project: Project)
        : AbstractExternalSystemLocalSettings(SYSTEM_ID, project, PlatformFacadeImpl()) {
}//