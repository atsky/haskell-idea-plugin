package org.jetbrains.cabal

import com.intellij.openapi.externalSystem.task.ExternalSystemTaskManager
import com.intellij.openapi.externalSystem.service.project.ExternalSystemProjectResolver
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.ide.actions.OpenProjectFileChooserDescriptor

import com.intellij.openapi.externalSystem.ExternalSystemManager
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.model.settings.ExternalSystemExecutionSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.SimpleJavaParameters
import com.intellij.util.Function
import org.jetbrains.haskell.icons.HaskellIcons
import org.jetbrains.cabal.settings.*
import org.jetbrains.cabal.export.*
import org.jetbrains.cabal.util.*

import javax.swing.*
//import java.util.List
import java.net.URL


public class CabalManager()
        : ExternalSystemManager<CabalProjectSettings, CabalSettingsListener, CabalSettings, CabalLocalSettings, ExternalSystemExecutionSettings> {

    override fun getSystemId(): ProjectSystemId {
        return SYSTEM_ID
    }

    override fun getSettingsProvider(): Function<Project, CabalSettings> {
        return Function<Project, CabalSettings> { CabalSettings(it!!) }
    }

    override fun getLocalSettingsProvider(): Function<Project, CabalLocalSettings> {
        return Function<Project, CabalLocalSettings> { CabalLocalSettings.getInstance(it!!) }
    }

    override fun getExecutionSettingsProvider(): Function<Pair<Project, String>, ExternalSystemExecutionSettings> {
        return Function<Pair<Project, String>, ExternalSystemExecutionSettings> { ExternalSystemExecutionSettings() }
    }

    throws(javaClass<ExecutionException>())
    override fun enhanceRemoteProcessing(parameters: SimpleJavaParameters) { }

    override fun enhanceLocalProcessing(urls: List<URL>) {  }

    override fun getProjectResolverClass(): Class<out ExternalSystemProjectResolver<ExternalSystemExecutionSettings>> {
        return javaClass<CabalProjectResolver>()
    }

    override fun getTaskManagerClass(): Class<out ExternalSystemTaskManager<ExternalSystemExecutionSettings>> {
        return javaClass<CabalTaskManager>()
    }

    override fun getExternalProjectDescriptor(): FileChooserDescriptor {
        return OpenProjectFileChooserDescriptor(true)
    }
}

