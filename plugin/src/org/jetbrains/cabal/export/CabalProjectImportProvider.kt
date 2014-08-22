package org.jetbrains.cabal.export

import com.intellij.projectImport.ProjectImportProvider
//import org.jetbrains.cabal.export.CabalProjectImportBuilder
import com.intellij.ide.util.projectWizard.ModuleImportProvider
import com.intellij.ide.util.projectWizard.ModuleImportBuilder
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext

public class CabalProjectImportProvider(): ProjectImportProvider(CabalProjectImportBuilder()) {

    public override fun canImport(fileOrDirectory: VirtualFile?, project: Project?): Boolean {
        return project != null && fileOrDirectory != null && !fileOrDirectory.isDirectory() && "cabal".equals(fileOrDirectory.getExtension());
    }

    public override fun getPathToBeImported(file: VirtualFile?): String? {
        return file?.getPath();
    }

    public override fun canCreateNewProject(): Boolean {
        return false;
    }

    public override fun createSteps(context: WizardContext?): Array<ModuleWizardStep> {
        return ModuleWizardStep.EMPTY_ARRAY;
    }

    public override fun getFileSample(): String? {
        return null;
    }
}