package org.jetbrains.cabal.export

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportProvider
import org.jetbrains.cabal.util.*


class CabalProjectImportProvider(builder: CabalProjectImportBuilder): AbstractExternalProjectImportProvider(builder, SYSTEM_ID) {


    override fun canImport(fileOrDirectory: VirtualFile, project: Project?): Boolean {
        return !fileOrDirectory.isDirectory && ("cabal".equals(fileOrDirectory.extension))
    }

    public override fun canImportFromFile(file: VirtualFile?): Boolean {
        return "cabal".equals(file?.extension)
    }

    override fun getPathToBeImported(file: VirtualFile?): String? {
        if (file == null)       return null
        if (file.isDirectory) return file.path
        return file.parent!!.path
    }

    override fun canCreateNewProject(): Boolean {
        return true
    }

    override fun createSteps(context: WizardContext?): Array<ModuleWizardStep> {
//        return array(ExternalModuleSettingsStep(CabalModuleBuilder(), CabalProjectSettingsControl(CabalProjectSettings())))
        return arrayOf(SimpleCabalStep(context!!))
    }

    override fun getFileSample(): String? {
        return "<b>Cabal</b> project file (*.cabal)"
    }
}