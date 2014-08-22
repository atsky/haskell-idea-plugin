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

public class CabalProjectImportBuilder<T>(): ProjectImportBuilder<T>() {

    override fun getName(): String {
        return ""
    }

    override fun getIcon(): Icon? {
        return null
    }

    override fun getList(): MutableList<T>? {
        return null
    }

    override fun isMarked(element: T?): Boolean {
        return false
    }

    throws(javaClass<ConfigurationException>())
    override fun setList(list: List<T>?) {
    }

    override fun setOpenProjectSettingsAfter(on: Boolean) {
    }

    override fun commit(project: Project?, model: ModifiableModuleModel?, modulesProvider: ModulesProvider?, artifactModel: ModifiableArtifactModel?): MutableList<Module>? {
        return null
    }
}