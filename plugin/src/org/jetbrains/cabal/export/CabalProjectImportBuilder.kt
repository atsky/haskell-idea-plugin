package org.jetbrains.cabal.export

import com.intellij.projectImport.ProjectImportBuilder
import javax.swing.Icon
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.packaging.artifacts.ModifiableArtifactModel
import com.intellij.ide.util.newProjectWizard.modes.ImportImlMode
import org.jetbrains.haskell.icons.HaskellIcons
import com.intellij.openapi.projectRoots.SdkTypeId
import org.jetbrains.haskell.sdk.HaskellSdkType

public class CabalProjectImportBuilder(): ProjectImportBuilder<Boolean>() {

    override fun getName(): String {
        return "Cabal"
    }

    override fun getIcon(): Icon {
        return HaskellIcons.CABAL
    }

    override fun getList(): MutableList<Boolean>? {
        return null
    }

    override fun isMarked(element: Boolean?): Boolean {
        return false
    }

    throws(javaClass<ConfigurationException>())
    override fun setList(list: List<Boolean>?) {
    }

    override fun setOpenProjectSettingsAfter(on: Boolean) {
    }

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean {
        return sdkType is HaskellSdkType
    }

    override fun commit(project: Project?, model: ModifiableModuleModel?, modulesProvider: ModulesProvider?, artifactModel: ModifiableArtifactModel?): MutableList<Module>? {
        return null
    }
}