package org.jetbrains.cabal.export

import com.intellij.projectImport.ProjectImportProvider
import com.intellij.ide.util.projectWizard.ModuleImportProvider
import com.intellij.ide.util.projectWizard.ModuleImportBuilder
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportProvider
import com.intellij.openapi.externalSystem.service.project.wizard.ExternalModuleSettingsStep
import org.jetbrains.cabal.export.CabalModuleBuilder
import org.jetbrains.cabal.settings.CabalProjectSettings
import org.jetbrains.cabal.util.*
import java.util.ArrayList

import org.jetbrains.haskell.module.HaskellModuleBuilder

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.externalSystem.model.ProjectSystemId

public class CabalProjectImportProvider(builder: CabalProjectImportBuilder): AbstractExternalProjectImportProvider(builder, SYSTEM_ID) {

    public override fun canImport(fileOrDirectory: VirtualFile?, project: Project?): Boolean {
        return (fileOrDirectory != null) && !fileOrDirectory.isDirectory() && ("cabal".equals(fileOrDirectory.getExtension()));
    }

    public override fun canImportFromFile(file: VirtualFile?): Boolean {
        return "cabal".equals(file?.getExtension())
    }

    public override fun getPathToBeImported(file: VirtualFile?): String? {
        if (file == null)       return null
        if (file.isDirectory()) return file.getPath()
        return file.getParent()!!.getPath()
    }

    public override fun canCreateNewProject(): Boolean {
        return true;
    }

    public override fun createSteps(context: WizardContext?): Array<ModuleWizardStep> {
        return array(ExternalModuleSettingsStep(CabalModuleBuilder(), CabalProjectSettingsControl(CabalProjectSettings())))
    }

    public override fun getFileSample(): String? {
        return "<b>Cabal</b> project file (*.cabal)";
    }
}