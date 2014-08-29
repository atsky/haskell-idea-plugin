package org.jetbrains.cabal.export

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportBuilder
//import com.intellij.openapi.externalSystem.service.project.manage.ProjectDataManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.SdkTypeId
import org.jetbrains.haskell.icons.HaskellIcons
import org.jetbrains.haskell.sdk.HaskellSdkType
import com.intellij.openapi.externalSystem.model.project.ProjectData
//import org.jetbrains.cabal.settings.CabalProjectSettings
import com.intellij.openapi.externalSystem.model.DataNode
import org.jetbrains.cabal.util.*
import javax.swing.Icon
import java.io.File
//
//import com.intellij.ide.util.projectWizard.WizardContext
//import com.intellij.openapi.components.PersistentStateComponent
//import com.intellij.openapi.diagnostic.Logger
//import com.intellij.openapi.externalSystem.model.DataNode
//import com.intellij.openapi.externalSystem.model.ExternalSystemDataKeys
//import com.intellij.openapi.externalSystem.model.ProjectSystemId
//import com.intellij.openapi.externalSystem.model.project.ProjectData
//import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
//import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
//import com.intellij.openapi.externalSystem.service.internal.ExternalSystemResolveProjectTask
//import com.intellij.openapi.externalSystem.service.project.ExternalProjectRefreshCallback
import com.intellij.openapi.externalSystem.service.project.manage.ProjectDataManager
//import com.intellij.openapi.externalSystem.service.settings.AbstractImportFromExternalSystemControl
//import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings
//import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings
//import com.intellij.openapi.externalSystem.util.DisposeAwareProjectChange
//import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
//import com.intellij.openapi.externalSystem.util.ExternalSystemBundle
//import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
//import com.intellij.openapi.module.ModifiableModuleModel
//import com.intellij.openapi.module.Module
//import com.intellij.openapi.options.ConfigurationException
//import com.intellij.openapi.progress.Task
//import com.intellij.openapi.project.ProjectManager
//import com.intellij.openapi.roots.ui.configuration.ModulesProvider
//import com.intellij.openapi.startup.StartupManager
//import com.intellij.packaging.artifacts.ModifiableArtifactModel
//import com.intellij.projectImport.ProjectImportBuilder
//import com.intellij.util.ui.UIUtil

public class CabalProjectImportBuilder(dataManager: ProjectDataManager)
        : AbstractExternalProjectImportBuilder<ImportFromCabalControl>(dataManager, ImportFromCabalControl(), SYSTEM_ID) {

    override fun getName(): String = "Cabal"

    override fun getIcon(): Icon   = HaskellIcons.CABAL

//    override fun getList(): MutableList<CabalProjectSettingsControl>? {
//        return arrayList(CabalProjectSettingsControl(CabalProjectSettings()))
//    }
//
//    override fun isMarked(element: CabalProjectSettingsControl?): Boolean {
//        return false
//    }

//    throws(javaClass<ConfigurationException>())
//    override fun setList(list: List<CabalProjectSettingsControl>?) {
//    }
//
//    override fun setOpenProjectSettingsAfter(on: Boolean) {
//    }

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean {
        return sdkType is HaskellSdkType
    }

//    override fun commit(project: Project?, model: ModifiableModuleModel?, modulesProvider: ModulesProvider?, artifactModel: ModifiableArtifactModel?): MutableList<Module>? {
//        return null
//    }

    override fun doPrepare(context: WizardContext) {
    }

    override fun beforeCommit(dataNode: DataNode<ProjectData>, project: Project) {
    }

    override fun applyExtraSettings(context: WizardContext) {
    }

    override fun getExternalProjectConfigToUse(file: File): File {
        return if (file.isDirectory()) file else file.getParentFile()!!
    }
}